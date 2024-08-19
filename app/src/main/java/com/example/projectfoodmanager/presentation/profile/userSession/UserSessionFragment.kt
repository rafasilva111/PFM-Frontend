package com.example.projectfoodmanager.presentation.profile.userSession

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.util.ValidationError
import com.example.projectfoodmanager.databinding.FragmentSessionProfileBinding
import com.example.projectfoodmanager.presentation.follower.FollowerFragment
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class UserSessionFragment : Fragment() {


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
        loadUserImage(binding.profileIV, user.imgSource)

        binding.nameTV.text =  getString(R.string.full_name, user.name)

        if (user.userType!=UserType.VIP)
            binding.premiumLL.visibility = View.GONE

        if (!user.verified)
            binding.verifyUserIV.visibility = View.INVISIBLE


        binding.nFollowedsTV.text = user.follows.toString()
        binding.nFollowersTV.text = user.followers.toString()

        /**
         * Image offline
         */

        loadUserImage(binding.profileIV, user.imgSource)

    }
    private fun setUI() {

        /**
         *  General
         * */
        val activity = requireActivity()
        changeMenuVisibility(true,activity)
        changeTheme(false, activity, context)

        /**
         * Buttons
         */

        binding.logoutCV.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle(getString(R.string.profile_fragment_logout_dialog_title))
                .setMessage(resources.getString(R.string.logout_confirmation_description))
                .setPositiveButton(getString(R.string.COMMON_DIALOG_YES)) { _, _ ->
                    // Adicione aqui o código para apagar o registro
                    userViewModel.logoutUser()
                }
                .setNegativeButton(getString(R.string.COMMON_DIALOG_NO)) { dialog, _ ->
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
                putString("follow_type",FollowerFragment.Companion.SelectedTab.FOLLOWS)
                putString("userName",user.name)
            })
            changeMenuVisibility(false,activity)
        }

        binding.followersLL.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followerFragment,Bundle().apply {
                putString("follow_type",FollowerFragment.Companion.SelectedTab.FOLLOWERS)
                putString("userName",user.name)
            })
            changeMenuVisibility(false,activity)
        }
    }


    private fun showValidationErrors(error: String) {
        toast(resources.getString(R.string.txt_error_message, error))
    }

    private fun showValidationErrors(error: ValidationError) {

        var stringBuilder = ""
        val errorsFlat = mutableListOf<String>()

        for ((type,messages) in error.errors.entries){
            stringBuilder +="Error: $type\n"
            for (message in messages){
                stringBuilder +="$message\n"
                errorsFlat.add(stringBuilder)
            }
            stringBuilder+="\n"
        }

        if (ErrorTypes.INTERNAL.code in error.errors && "Token is blacklisted" in error.errors[ErrorTypes.INTERNAL.code]!!){
            deleteSessionData()
            navigateToLoginView()
        }


        toast(stringBuilder,ToastType.ERROR)
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
                        toast("Logout feito com sucesso!")
                        deleteSessionData()
                        navigateToLoginView()
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.error!!)
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

    private fun navigateToLoginView() {
        findNavController().navigate(R.id.action_profile_to_login)
        changeMenuVisibility(false, activity)
    }

    private fun deleteSessionData() {
        tokenManager.deleteSession()
        sharedPreference.deleteSession()
    }


}