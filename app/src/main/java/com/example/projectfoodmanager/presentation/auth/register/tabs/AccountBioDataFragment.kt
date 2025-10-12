
package com.example.projectfoodmanager.presentation.auth.register.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.databinding.FragmentRegisterBiodataBinding
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment.Companion.imgURI
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class AccountBioDataFragment(private val parentBinding: FragmentRegisterBinding) : Fragment() {

    /** binding */
    lateinit var binding: FragmentRegisterBiodataBinding

    /** viewModels */
    val userViewModel: UserViewModel by viewModels()

    /** constants */
    val TAG: String = "BioDataFragment"
    private var activityLevel : Double = 0.0

    /** injects */

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference
    /** adapters */


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBiodataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    private fun setUI(){

        /**
         *  Navigation
         * */

        binding.backBtn.setOnClickListener {

            val currentTab = parentBinding.fragmentRegisterTabLayout.selectedTabPosition
            val nextTab = currentTab - 1

            parentBinding.fragmentRegisterViewPager.currentItem= nextTab

        }

        binding.finishBtn.setOnClickListener {
            if (validation()) {
                patchUser()
                if (imgURI != null){
                    val path = "${FireStorage.user_profile_images}${UUID.randomUUID().toString() +".jpg"}"
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



        /**
         *  Validations
         * */


        binding.activityLevelRg.setOnCheckedChangeListener { _, checkedId ->
            validateActivityLevel()
            when(checkedId){
                R.id.op1_RB-> activityLevel= 1.2
                R.id.op2_RB-> activityLevel= 1.375
                R.id.op3_RB-> activityLevel= 1.465
                R.id.op4_RB-> activityLevel= 1.55
                R.id.op5_RB-> activityLevel= 1.725
                R.id.op6_RB-> activityLevel= 1.9
            }
        }

        binding.weightEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val weightString = binding.weightEt.text.toString()
                val weight = weightString.toFloatOrNull()

                when {
                    weightString.isBlank() -> {
                        binding.weightTL.isErrorEnabled = true
                        binding.weightTL.error = getString(R.string.USER_ERROR_WEIGHT_INVALID)
                    }
                    weight == null || weight !in 30F..200F -> {
                        binding.weightTL.isErrorEnabled = true
                        binding.weightTL.error = getString(R.string.USER_ERROR_WEIGHT_INVALID_2)
                    }
                    else -> {
                        binding.weightTL.isErrorEnabled = false
                    }
                }

            }
        }

        binding.heightEt.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus){
                val heightString = binding.heightEt.text.toString()
                var height = heightString.toFloatOrNull()
                if (height != null && height in 1.0..3.0){
                    height *= 100F
                    binding.heightEt.setText(height.toString())
                }


                when {
                    heightString.isBlank() -> {
                        binding.heightTL.isErrorEnabled = true
                        binding.heightTL.error = getString(R.string.USER_ERROR_HEIGHT_INVALID)
                    }
                    height == null || height !in 100.0..300.0 -> {
                        binding.heightTL.isErrorEnabled = true
                        binding.heightTL.error = getString(R.string.USER_ERROR_HEIGHT_INVALID_2)
                    }
                    else -> {
                        binding.heightTL.isErrorEnabled = false
                    }
                }
            }
        }



    }

    private fun patchUser() {

        /** Activity Level  */
        RegisterFragment.user.activityLevel = activityLevel

        /** Weight  */
        RegisterFragment.user.weight =  binding.weightEt.text.toString().toFloat()

        /** Height  */
        RegisterFragment.user.height = binding.heightEt.text.toString().toFloat()
    }

    private fun validation(): Boolean {
        var isValid = true

        val heightString = binding.heightEt.text.toString()
        val height = heightString.toFloatOrNull()

        when {
            heightString.isBlank() -> {
                binding.heightTL.isErrorEnabled = true
                binding.heightTL.error = getString(R.string.USER_ERROR_HEIGHT_INVALID)
            }
            height == null || (height !in 120.0..300.0 && height !in 1.20..3.0) -> {
                binding.heightTL.isErrorEnabled = true
                binding.heightTL.error = getString(R.string.USER_ERROR_HEIGHT_INVALID_2)
            }
            else -> {
                if (height in 1.20..3.0)
                    binding.heightEt.setText((height * 100).toString())

                binding.heightTL.isErrorEnabled = false
            }
        }

        val weightString = binding.weightEt.text.toString()
        val weight = weightString.toFloatOrNull()

        when {
            weightString.isBlank() -> {
                binding.weightTL.isErrorEnabled = true
                binding.weightTL.error = getString(R.string.USER_ERROR_WEIGHT_INVALID)
                isValid = false
            }
            weight == null || weight !in 40F..150F -> {
                binding.weightTL.isErrorEnabled = true
                binding.weightTL.error = getString(R.string.USER_ERROR_WEIGHT_INVALID_2)
                isValid = false
            }
            else -> {
                binding.weightTL.isErrorEnabled = false
            }
        }

        isValid = validateActivityLevel() and isValid

        return isValid
    }

    private fun validateActivityLevel():Boolean{

        var isValid = true
        if (binding.activityLevelRg.checkedRadioButtonId == -1) {
            isValid = false

            binding.op1RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op1RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op2RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op2RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op3RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op3RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op4RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op4RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op5RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op5RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op6RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op6RB.setTextColor(resources.getColor(R.color.red,null))

            binding.errorActivityLevelTV.visibility=View.VISIBLE
            binding.errorActivityLevelTV.text=getString(R.string.USER_ERROR_ACTIVITY_LEVEL_INVALID)
        }else{
            binding.op1RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op1RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op2RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op2RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op3RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op3RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op4RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op4RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op5RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op5RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op6RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op6RB.setTextColor(resources.getColor(R.color.black,null))
            binding.errorActivityLevelTV.visibility=View.INVISIBLE
        }
        return isValid
    }

    private fun goBack() {
        parentBinding.fragmentRegisterViewPager.currentItem = parentBinding.fragmentRegisterTabLayout.selectedTabPosition - 1
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
                                        goBack()
                                    }
                                    "email" -> goBack()
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

}