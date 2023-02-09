package com.example.projectfoodmanager.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.LoginActivity
import com.example.projectfoodmanager.MainActivity
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.util.Resource
import com.example.projectfoodmanager.databinding.FragmentLoginBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
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

    fun observer(){

        authViewModel.successful.observe(viewLifecycleOwner) { successful ->
            if (successful == true){
                toast("Sucess")
                authViewModel.navigateToPage()
                startActivity(Intent(this.context, MainActivity::class.java))
            }else if(successful == false){
                if (authViewModel.error.value!!.contains("There is no user record corresponding to this identifier."))
                    toast(getString(R.string.invalid_email_3))
                else if (authViewModel.error.value!!.contains("User's password is incorrect"))
                    toast(getString(R.string.invalid_password_1))
                else if (authViewModel.error.value!!.contains("You can immediately restore it by resetting your password or you can try again later."))
                    toast(getString(R.string.to_many_attemps_to_login_failed))
                else
                    toast("Failure")
                authViewModel.navigateToPage()
            }
        }
        authViewModel.user.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Loading -> {
                    Log.i(TAG,"Loading...")
                }
                is Resource.Success -> {
                    response.data
                    val user = response.data
                    Log.i(TAG,"${response.data}")
                }
                is Resource.Error -> {
                    if (response.code == ERROR_CODES.SESSION_INVALID){
                        startActivity(Intent(this.context, LoginActivity::class.java))
                        //todo delete user prefs
                        toast(getString(R.string.invalid_session))
                    }
                    Log.i(TAG,"${response.message}")
                }
            }
        }
    }



    fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_email))
        }else{
            if (!binding.emailEt.text.toString().isValidEmail()){
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.passEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_password))
        }else{
            if (binding.passEt.text.toString().length < 8){
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }

}