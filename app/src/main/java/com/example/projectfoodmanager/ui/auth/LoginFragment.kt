package com.example.projectfoodmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.projectfoodmanager.MainActivity
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FirstTimeWelcomingBinding
import com.example.projectfoodmanager.databinding.FragmentLoginBinding
import com.example.projectfoodmanager.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    val TAG: String = "RegisterFragment"
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
                authViewModel.login(
                    email = binding.emailEt.text.toString().trim(),
                    password = binding.passEt.text.toString().trim()
                )
            }
        }

        binding.forgotPassLabel.setOnClickListener {

        }
        binding.registerLabel.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    fun observer(){
        authViewModel.login.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.loginBtn.setText("")
                    binding.loginProgress.show()
                }
                is UiState.Failure -> {
                    binding.loginBtn.setText("Login")
                    binding.loginProgress.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.loginBtn.setText("Login")
                    binding.loginProgress.hide()
                    toast(state.data)
                    startActivity(Intent(this.context, MainActivity::class.java))
                }
            }
        }
        authViewModel.getUserSession.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                }
                is UiState.Failure -> {
                    authViewModel.getMetadata {
                        var metadata = it?.get(MetadataConstants.FIRST_TIME_LOGIN)
                        if (metadata == null) {
//                            binding = Fragment.inflate(layoutInflater)
//                            setContentView(R.layout.first_time_welcoming)
//                            findViewById<Button>(R.id.btn_continue).setOnClickListener {
//                                setContentView(R.layout.activity_login)
//                            }
                            authViewModel.storeMetadata(
                                MetadataConstants.FIRST_TIME_LOGIN,
                                true.toString()
                            ) {
                            }
                        }

                    }
                }
                is UiState.Success -> {
                    toast("Bem-vindo de volta!")
                    startActivity(Intent(this.context, MainActivity::class.java))
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