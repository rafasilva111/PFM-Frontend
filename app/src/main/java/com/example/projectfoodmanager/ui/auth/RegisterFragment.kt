package com.example.projectfoodmanager.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.MainActivity
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    val TAG: String = "RegisterFragment"
    lateinit var binding: FragmentRegisterBinding
    val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.registerBtn.setOnClickListener {
            if (validation()){
                viewModel.register(
                    email = binding.emailEt.text.toString(),
                    password = binding.passEt.text.toString(),
                    user = getUserObj()
                )
            }
        }
    }

    fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.registerBtn.setText("")
                    binding.registerProgress.show()
                }
                is UiState.Failure -> {
                    binding.registerBtn.setText("Register")
                    binding.registerProgress.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.registerBtn.setText("Register")
                    binding.registerProgress.hide()
                    toast(state.data)
                    findNavController().navigate(R.id.action_registerFragment_to_home_navigation)
                }
            }
        }
    }

    fun getUserObj(): User {
        return User(
            id = "",
            first_name = binding.firstNameEt.hint.toString(),
            last_name = binding.lastNameEt.text.toString(),
            email = binding.emailEt.text.toString(),
        )
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.firstNameEt.hint.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_first_name))
        }

        if (binding.lastNameEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_last_name))
        }

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