package com.example.projectfoodmanager.presentation.profile

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.projectfoodmanager.MainActivity
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentProfileBinding
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.util.TokenManager
import com.example.projectfoodmanager.util.toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    val TAG: String = "ProfileFragment"

    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        binding.logoutIB.setOnClickListener {

            //USER CONFIRMATION DIALOG
            // set the custom layout
            val dialogBinding : View = layoutInflater.inflate(R.layout.dialog_confirmation_from_user, null);

            val myDialog = Dialog(requireContext())
            myDialog.setContentView(dialogBinding)

            // create alert dialog
            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val yesBtn = dialogBinding.findViewById<Button>(R.id.btn_conf_Yes)
            val cancelBtn = dialogBinding.findViewById<Button>(R.id.btn_conf_cancel)

            yesBtn.setOnClickListener {
                authViewModel.logoutUser()
                myDialog.dismiss()
            }

            cancelBtn.setOnClickListener {
                myDialog.dismiss()
            }

            myDialog.show()

        }

        binding.favoritesCV.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_favorites)
        }

        binding.likeCV.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_favorites,Bundle().apply {
                putString("aba","gostos")
            })
        }

        binding.settingsCV.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }



        val userSession: User? = sharedPreference.getUserSession()

        // load profile image offline

        if (isOnline(view.context)) {

            // load profile image online

            if (userSession?.img_source != null && userSession.img_source != "") {
                val imgRef = Firebase.storage.reference.child(userSession.img_source)
                imgRef.downloadUrl.addOnSuccessListener { Uri ->
                    val imageURL = Uri.toString()
                    Glide.with(binding.ivProfilePicOnProfile.context).load(imageURL)
                        .into(binding.ivProfilePicOnProfile)
                }
                    .addOnFailureListener {
                        Glide.with(binding.ivProfilePicOnProfile.context)
                            .load(R.drawable.good_food_display___nci_visuals_online)
                            .into(binding.ivProfilePicOnProfile)
                    }
            }

            // get followers

            authViewModel.getUserFollowees()
            authViewModel.getUserFollowers()

        }

    }

    private fun showValidationErrors(error: String) {
        toast(String.format(resources.getString(R.string.txt_error_message, error)))
    }

    private fun bindObservers() {
        authViewModel.userLogoutResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        tokenManager.deleteToken()
                        sharedPreference.deleteUserSession()
                        toast("Logout feito com sucesso!")
                        findNavController().navigate(R.id.action_profile_to_login)
                        changeVisib_Menu(false)
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        })
    }

    private fun changeVisib_Menu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

}