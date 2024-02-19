package com.example.projectfoodmanager.presentation.profile.userSession

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.AvatarGVAdapter
import com.example.projectfoodmanager.BuildConfig
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.FragmentSessionProfileBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.FireStorage.user_profile_images
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.example.projectfoodmanager.util.ActionResultCodes.GALLERY_REQUEST_CODE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yalantis.ucrop.UCrop

import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class SessionProfileFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentSessionProfileBinding

    // viewModels
    private val userViewModel by activityViewModels<UserViewModel>()

    // constants
    private val TAG: String = "SessionProfileFragment"
    private lateinit var user: User

    // injects
    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentSessionProfileBinding.inflate(layoutInflater)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (isOnline(requireView().context)) {
            userViewModel.getUserSession()
        }else{
            user = sharedPreference.getUserSession()
            loadUI()
        }

        setUI()


        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }


    private fun loadUI(){
        /**
         * Info
         */
        //Set Profile Image
        loadUserImage(binding.profileIV, user.img_source)

        binding.nameTV.text =  getString(R.string.full_name, user.name)

        if (user.user_type!=UserType.VIP)
            binding.premiumLL.visibility = View.GONE

        if (!user.verified)
            binding.verifyUserIV.visibility = View.INVISIBLE


        binding.nFollowedsTV.text = user.followeds.toString()
        binding.nFollowersTV.text = user.followers.toString()

        /**
         * Image offline
         */

        loadUserImage(binding.profileIV, user.img_source)

    }
    private fun setUI() {

        /**
         *  General
         * */
        val activity = requireActivity()
        changeMenuVisibility(true,activity)
        changeStatusBarColor(false, activity, context)

        /**
         * Buttons
         */

        binding.logoutCV.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle(getString(R.string.profile_fragment_logout_dialog_title))
                .setMessage(resources.getString(R.string.logout_confirmation_description))
                .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                    // Adicione aqui o código para apagar o registro
                    userViewModel.logoutUser()
                }
                .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
                    // Adicione aqui o código para cancelar a exclusão do registro
                    dialog.dismiss()
                }
                .show()

        }

        binding.myProfileCV.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_session_profile_to_sessionAccountProfileFragment)
        }

        binding.shoppingListsCV.setOnClickListener {
            changeMenuVisibility(false,activity)
            findNavController().navigate(R.id.action_profileFragment_to_shoppingListListingFragment)
        }

        binding.myRecipesCV.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_favorites,Bundle().apply {
                putString("chip",getString(R.string.tab_created))
            })
        }

        binding.settingsCV.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        binding.followedsLL.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followerFragment,Bundle().apply {
                putInt("userID",-1)
                putInt("followType",FollowType.FOLLOWEDS)
                putString("userName",user.name)
            })
            changeMenuVisibility(false,activity)
        }

        binding.followersLL.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followerFragment,Bundle().apply {
                putInt("userID",-1)
                putInt("followType",FollowType.FOLLOWERS)
                putString("userName",user.name)
            })
            changeMenuVisibility(false,activity)
        }
    }


    private fun showValidationErrors(error: String) {
        toast(String.format(resources.getString(R.string.txt_error_message, error)))
    }

    private fun bindObservers() {
        userViewModel.userResponseLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        user = result.data!!
                        loadUI()
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }


        userViewModel.userLogoutResponseLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        tokenManager.deleteToken()
                        sharedPreference.deleteUserSession()
                        toast("Logout feito com sucesso!")
                        findNavController().navigate(R.id.action_profile_to_login)
                        changeMenuVisibility(false, activity)
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }

        userViewModel.userUpdateResponseLiveData.observe(viewLifecycleOwner) { userSessionResponse ->
            userSessionResponse.getContentIfNotHandled()?.let {

                when (it) {
                    is NetworkResult.Success -> {
                        toast("Dados atualizados com sucesso")

                    }
                    is NetworkResult.Error -> {
                        toast("Dados não atualizados, alguma coisa se passou.",ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        // show loading bar
                        //todo falta aqui uma loading bar

                    }
                }
            }
        }


    }



}