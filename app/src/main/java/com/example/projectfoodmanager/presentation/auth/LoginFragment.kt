package com.example.projectfoodmanager.presentation.auth

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.databinding.FragmentLoginBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.example.projectfoodmanager.viewmodels.CalendarViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {


    lateinit var binding: FragmentLoginBinding

    val TAG: String = "LoginFragment"



    private val userViewModel by activityViewModels<UserViewModel>()
    private val calendarViewModel by activityViewModels<CalendarViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentLoginBinding.inflate(layoutInflater)
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()

        super.onViewCreated(view, savedInstanceState)

        bindObservers()
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()){
            isValid = false
            binding.emailTL.isErrorEnabled=true
            binding.emailTL.error=getString(R.string.enter_email)
            //toast(getString(R.string.enter_email))
        }else if (!binding.emailEt.text.toString().isValidEmail()){
            isValid = false
            binding.emailTL.isErrorEnabled=true
            binding.emailTL.error=getString(R.string.invalid_email)
            //toast(getString(R.string.invalid_email))

        }else{
            binding.emailTL.isErrorEnabled=false
        }

        if (binding.passEt.text.isNullOrEmpty()){
            isValid = false
            binding.passwordTL.isErrorEnabled=true
            binding.passwordTL.error=getString(R.string.enter_password)
            //toast(getString(R.string.enter_password))
        }else if (binding.passEt.text.toString().length < 8){
            isValid = false
            binding.passwordTL.isErrorEnabled=true
            binding.passwordTL.error=getString(R.string.invalid_password)
            //toast(getString(R.string.invalid_password))
        }else{
            binding.passwordTL.isErrorEnabled=false
        }
        return isValid
    }

    private fun showValidationErrors(error: String) {
        Toast(context).showCustomToast(error,requireActivity(),ToastType.ERROR)
       // toast(error)
    }

    private fun setButtonVisibility(visibility: Boolean) {
        if (visibility){
            binding.loginBtn.isVisible = true
            binding.progressBar.isVisible = false
        }
        else{
            binding.loginBtn.isVisible = false
            binding.progressBar.isVisible = true
        }

    }

    private fun setUI() {

        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        changeTheme(true,activity,requireContext())


        /**
         * Navigation
         */

        binding.loginBtn.setOnClickListener {
            if (validation()) {
                userViewModel.loginUser(binding.emailEt.text.toString().trim(),binding.passEt.text.toString().trim())
            }
        }


        binding.forgotPassLabel.setOnClickListener {

        }

        binding.registerBtn.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

    }

    private fun bindObservers() {
        userViewModel.authTokenLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->

                when (result) {
                    is NetworkResult.Success -> {
                        tokenManager.saveToken(result.data!!)
                        userViewModel.getUserSession()
                    }
                    is NetworkResult.Error -> {
                        binding.loginBtn.isVisible = true
                        binding.progressBar.isVisible = false
                        showValidationErrors(result.message.toString())
                        setButtonVisibility(visibility = true)
                    }
                    is NetworkResult.Loading -> {
                        setButtonVisibility(visibility = false)
                    }
                }
            }
        }

        userViewModel.userResponseLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        if ( result.data!!.fmcToken != "-1")
                            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                                if (result.data.fmcToken != token)
                                    userViewModel.updateUser(UserDTO(fmc_token = token))
                            }

                        LocalDateTime.now().let { dateNow ->
                            calendarViewModel.getCalendarDatedEntryList(
                                fromDate = dateNow.minusDays(15),
                                toDate = dateNow.plusDays(15),
                                cleanseOldRegistry = true
                            )
                        }

                        userViewModel.getUserRecipesBackground()

                        setButtonVisibility(visibility = true)
                        findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
                    }
                    is NetworkResult.Error -> {
                        binding.loginBtn.isVisible = true
                        binding.progressBar.isVisible = false
                        showValidationErrors(result.message.toString())
                        setButtonVisibility(visibility = true)

                    }
                    is NetworkResult.Loading -> {

                    }
                }
            }
        }
    }


}