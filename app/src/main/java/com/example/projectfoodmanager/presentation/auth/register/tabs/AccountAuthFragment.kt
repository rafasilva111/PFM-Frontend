package com.example.projectfoodmanager.presentation.auth.register.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentRegisterAccountDetailsBinding
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment.Companion.imgURI
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AccountAuthFragment(private var parentBinding: FragmentRegisterBinding) : Fragment() {


    /** binding */
    private lateinit var binding: FragmentRegisterAccountDetailsBinding

    /** viewModels */
    private val userViewModel by activityViewModels<UserViewModel>()

    /** variables */
    private val TAG: String = "BlankFragment"


    /** injects */
    @Inject
    lateinit var sharedPreference: SharedPreference

    /** adapters */




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentRegisterAccountDetailsBinding.inflate(layoutInflater)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    private fun setUI() {

        /**
         *  General
         * */


        /**
         *  Navigation
         * */

        binding.continueBtn.setOnClickListener {
            if (validation()){
                patchUser()
                val currentTab = parentBinding.fragmentRegisterTabLayout.selectedTabPosition
                val nextTab = currentTab + 1

                parentBinding.fragmentRegisterViewPager.currentItem= nextTab
            }
        }

        binding.backBtn.setOnClickListener {
            goBack()
        }

        /**
         *  Validations
         * */


        binding.userNameET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                if (binding.userNameET.text.isNullOrEmpty()){
                    binding.userNameTL.isErrorEnabled=true
                    binding.userNameTL.error=getString(R.string.enter_first_name)
                }else{
                    binding.userNameTL.isErrorEnabled=false
                    // todo check if username is avaible
                }
            }
        }

        binding.emailET.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                if (binding.emailET.text.isNullOrEmpty()){
                    binding.emailTL.isErrorEnabled=true
                    binding.emailTL.error=getString(R.string.enter_email)
                }else if (!binding.emailET.text.toString().isValidEmail()){
                    binding.emailTL.isErrorEnabled=true
                    binding.emailTL.error=getString(R.string.invalid_email)
                }else{
                    binding.emailTL.isErrorEnabled=false
                }
            }
        }


        binding.passEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){

                if (binding.passEt.text.isNullOrEmpty()){
                    binding.passwordTL.isErrorEnabled=true
                    binding.passwordTL.error=getString(R.string.enter_password)
                    //toast(getString(R.string.enter_password))
                }else if (binding.passEt.text.toString().length < 8){
                    binding.passwordTL.isErrorEnabled=true
                    binding.passwordTL.error=getString(R.string.invalid_password_1)

                }
                else{
                    binding.passwordConfTL.isErrorEnabled=false
                }
            }
        }

        binding.passEtConf.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                if(binding.passEt.text.toString().compareTo(binding.passEtConf.text.toString()) != 0 ){
                    binding.passwordConfTL.isErrorEnabled=true
                    binding.passwordConfTL.error=getString(R.string.invalid_password_2)
                }else{
                    binding.passwordConfTL.isErrorEnabled=false
                }
            }
        }

        /** Actions */

        binding.skipBiodata.setOnClickListener{

            if (validation()) {
                patchUser()
                if (imgURI != null) {
                    val path = "${FireStorage.user_profile_images}${UUID.randomUUID().toString() + ".jpg"}"
                    Firebase.storage.reference.child(path).putFile(imgURI!!)
                        .addOnSuccessListener {
                            RegisterFragment.user.img_source = path
                            userViewModel.registerUser(RegisterFragment.user)
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "uploadImageToFirebase: " + e)
                        }
                }
                else
                    userViewModel.registerUser(RegisterFragment.user)
            }
        }


    }

    private fun goBack() {
        parentBinding.fragmentRegisterViewPager.currentItem = parentBinding.fragmentRegisterTabLayout.selectedTabPosition - 1
    }

    private fun patchUser() {

        /** User Name  */
        RegisterFragment.user.userName = binding.userNameET.text.toString().trim()

        /** Email  */
        RegisterFragment.user.email = binding.emailET.text.toString().trim()

        /** Sex  */
        RegisterFragment.user.password = binding.passEt.text.toString().trim()
    }

    private fun bindObservers() {
        userViewModel.userRegisterLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        toast(getString(R.string.user_registered_successfully))
                    }
                    is NetworkResult.Error -> {
                        result.error?.let { it->

                            RegisterFragment.errors = it
                            for (item in it.errors)
                                when(item.key){
                                    "username" -> {
                                        if (parentBinding.fragmentRegisterViewPager.currentItem == 2)
                                            goBack()
                                        errorOnUsername(item.value[0])
                                    }
                                    "email" -> {
                                        if (parentBinding.fragmentRegisterViewPager.currentItem == 2)
                                            goBack()
                                        errorOnEmail(item.value[0])
                                    }
                                }
                        }
                    }
                    is NetworkResult.Loading -> {
                        // todo falta aqui um loading bar
                    }
                }
            }
        }
    }

    private fun validation(): Boolean {

        var isValid = true

        /** UserName  */

        if (binding.userNameET.text.isNullOrEmpty()){
            errorOnUsername(getString(R.string.enter_username))
            isValid = false
        }
        else
            binding.userNameTL.isErrorEnabled=false

        /** Email  */
        if (binding.emailET.text.isNullOrEmpty()){
            errorOnEmail(getString(R.string.enter_email))
            isValid = false
        }else if (!binding.emailET.text.toString().isValidEmail()){
            errorOnEmail(getString(R.string.invalid_email))
            isValid = false
        }else{
            binding.emailTL.isErrorEnabled=false
        }

        /** Password  */
        if(binding.passwordTL.isErrorEnabled)
            binding.passwordTL.isErrorEnabled=false

        if (binding.passEt.text.isNullOrEmpty()){
            isValid = false
            binding.passwordTL.isErrorEnabled=true
            binding.passwordTL.error=getString(R.string.enter_password)
        }else if (binding.passEt.text.toString().length < 8){
            isValid = false
            binding.passwordTL.isErrorEnabled=true
            binding.passwordTL.error=getString(R.string.invalid_password_1)

        }else if(binding.passEt.text.toString().compareTo(binding.passEtConf.text.toString()) != 0 ){
            isValid = false
            binding.passwordConfTL.isErrorEnabled=true
            binding.passwordConfTL.error=getString(R.string.invalid_password_2)
        }else{
            binding.passwordConfTL.isErrorEnabled=false
        }


        return isValid
    }

    private fun errorOnEmail(error: String){
        binding.emailTL.isErrorEnabled=true
        binding.emailTL.error=error
    }

    private fun errorOnUsername(error: String){
        binding.userNameTL.isErrorEnabled=true
        binding.userNameTL.error=error
    }



}