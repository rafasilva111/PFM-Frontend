package com.example.projectfoodmanager.presentation.auth

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import androidx.lifecycle.Observer
import com.example.projectfoodmanager.databinding.FragmentLoginBinding
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {

    val TAG: String = "LoginFragment"
    lateinit var binding: FragmentLoginBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo get user in shared preferences
        binding.progressBar.hide()

        bindObservers()
        binding.loginBtn.setOnClickListener {
            if (validation()) {
                authViewModel.loginUser(binding.emailEt.text.toString().trim(),binding.passEt.text.toString().trim())
            }
        }


        binding.forgotPassLabel.setOnClickListener {

        }
        binding.registerLabel.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    fun validation(): Boolean {
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

    override fun onResume() {
        super.onResume()
        //todo get user token from shared preferences
        //authViewModel.getUserSession()
        changeVisib_Menu(false)
    }

    private fun changeVisib_Menu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

    private fun showValidationErrors(error: String) {
        toast(error)
    }

    private fun bindObservers() {
        authViewModel.userAuthResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{

                when (it) {
                    is NetworkResult.Success -> {
                        tokenManager.saveToken(it.data!!.token)
                        authViewModel.getUserSession()
                    }
                    is NetworkResult.Error -> {
                        binding.loginBtn.isVisible = true
                        binding.progressBar.isVisible = false
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.loginBtn.isVisible = false
                        binding.progressBar.isVisible = true
                    }
                }
            }
        })

        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{

                when (it) {
                    is NetworkResult.Success -> {
                        Handler().postDelayed({
                            binding.loginBtn.isVisible = true
                            binding.progressBar.isVisible = false
                                if (it.data != null) {
                                        sharedPreference.saveUserSession(it.data)
                                        findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
                                }
                                else{
                                    Log.d(TAG, "userResponseLiveData Observer: Something went wrong")
                                }
                        }, LOGIN_TIME)
                    }
                    is NetworkResult.Error -> {
                        binding.loginBtn.isVisible = true
                        binding.progressBar.isVisible = false
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {

                    }
                }
            }
        })
    }
}