package com.example.projectfoodmanager.presentation.auth

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.util.Resource
import com.example.projectfoodmanager.databinding.FragmentLoginBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    val TAG: String = "LoginFragment"
    lateinit var binding: FragmentLoginBinding
    val authViewModel: AuthViewModel by viewModels()

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

        observer()
        binding.loginBtn.setOnClickListener {
            if (validation()) {
                authViewModel.login(binding.emailEt.text.toString().trim(),binding.passEt.text.toString().trim())
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




    fun observer(){

        authViewModel.login.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.loginBtn.setText("")
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    binding.loginBtn.setText("Login")
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    if (!binding.emailEt.text.isNullOrEmpty()){
                        binding.loginBtn.setText("Login")
                        binding.progressBar.hide()
                        toast(state.data)
                        findNavController().navigate(R.id.action_loginFragment_to_home_navigation)

                    }
                }
            }
        }
    }
}