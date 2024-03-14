package com.example.projectfoodmanager.presentation.goals

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.FragmentUpdateBiodataBinding
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class UpdateBiodataFragment : Fragment() {



    /** binding */
    lateinit var binding: FragmentUpdateBiodataBinding

    /** viewModels */
    val userViewModel: UserViewModel by viewModels()

    /** variables */
    val TAG: String = "UpdateBiodata"
    private lateinit var user : User
    private var activityLevel : Float = 0.0f

    private lateinit var onBackDialog: MaterialAlertDialogBuilder

    /** injects */
    @Inject
    lateinit var sharedPreference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentUpdateBiodataBinding.inflate(layoutInflater)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    override fun onStart() {
        loadUI()
        super.onStart()
    }


    private fun setUI() {


        /**
         *  General
         * */



        /** Sex Dropdown choices */
        binding.sexEt.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_register_gender,resources.getStringArray(R.array.gender_array)))

        /**
         *  Navigation
         * */

        /** onBack */

        onBackDialog = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_logout)
            .setTitle(getString(R.string.profile_fragment_logout_dialog_title))
            .setMessage(resources.getString(R.string.logout_confirmation_description))
            .setPositiveButton(getString(R.string.dialog_yes)) { dialog, _ ->
                findNavController().navigateUp()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
                dialog.dismiss()
            }

        binding.backIB.setOnClickListener {
            onBackDialog.show()
        }

        /** Update */


        binding.updateBtn.setOnClickListener {
            if (validation()) {
                userViewModel.updateUser(patchUser())
            }
        }

        /**
         *  Validations
         * */

        /** Sex  */
        binding.sexEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                if (binding.sexEt.text.isNullOrEmpty()){
                    binding.sexTL.isErrorEnabled=true
                    binding.sexTL.error=getString(R.string.invalid_sex)
                }else{
                    binding.sexTL.isErrorEnabled=false
                }

            }else{
                val imm = binding.sexTL.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        /** Activity Level  */
        binding.activityLevelRg.setOnCheckedChangeListener { _, checkedId ->
            validateActivityLevel()
            when (checkedId) {
                R.id.op1_RB -> activityLevel = 1.2F
                R.id.op2_RB -> activityLevel = 1.375F
                R.id.op3_RB -> activityLevel = 1.465F
                R.id.op4_RB -> activityLevel = 1.55F
                R.id.op5_RB -> activityLevel = 1.725F
                R.id.op6_RB -> activityLevel = 1.9F
            }

        }

        /** Weight  */
        binding.weightEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val weightString = binding.weightEt.text.toString()
                val weight = weightString.toFloatOrNull()

                when {
                    weightString.isBlank() -> {
                        binding.weightTL.isErrorEnabled = true
                        binding.weightTL.error = getString(R.string.enter_weight)
                    }
                    weight == null || weight !in 30F..200F -> {
                        binding.weightTL.isErrorEnabled = true
                        binding.weightTL.error = getString(R.string.weight_problem_1)
                    }
                    else -> {
                        binding.weightTL.isErrorEnabled = false
                    }
                }

            }
        }

        /** Height  */
        binding.heightEt.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus) {
                val heightString = binding.heightEt.text.toString()
                var height = heightString.toFloatOrNull()
                if (height != null && height in 1.0..3.0) {
                    height *= 100F
                    binding.heightEt.setText(height.toString())
                }


                when {
                    heightString.isBlank() -> {
                        binding.heightTL.isErrorEnabled = true
                        binding.heightTL.error = getString(R.string.enter_height)
                    }
                    height == null || height !in 100.0..300.0 -> {
                        binding.heightTL.isErrorEnabled = true
                        binding.heightTL.error = getString(R.string.height_problem_2)
                    }
                    else -> {
                        binding.heightTL.isErrorEnabled = false
                    }
                }
            }


        }
    }



    private fun loadUI() {

        /**
         *  Load Variables
         *
         *  Note:
         *      This Should be loaded as soon as possible in loadUI
         *
         * */


        user = sharedPreference.getUserSession()



        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        changeStatusBarColor(true, activity, requireContext())



        /** Biometric Data  */

        val genders = resources.getStringArray(R.array.gender_array)
        when(user.sex){
            SEX.M -> binding.sexEt.setText(genders[0], false)
            SEX.F-> binding.sexEt.setText(genders[1], false)
            else -> binding.sexEt.setText(genders[2], false)
        }

        binding.heightEt.setText(user.height.toInt().toString())
        binding.weightEt.setText(user.weight.toInt().toString())

        when(user.activity_level.toFloat()){
            1.2F -> binding.activityLevelRg.check(R.id.op1_RB)
            1.375F -> binding.activityLevelRg.check(R.id.op2_RB)
            1.465F -> binding.activityLevelRg.check(R.id.op3_RB)
            1.55F -> binding.activityLevelRg.check(R.id.op4_RB)
            1.725F -> binding.activityLevelRg.check(R.id.op5_RB)
            1.9F -> binding.activityLevelRg.check(R.id.op6_RB)
        }

    }

    private fun patchUser(): UserDTO {
        val userPatchData = UserDTO()

        /** Sex  */
        val genders = resources.getStringArray(R.array.gender_array)
        when(binding.sexEt.text.toString()){
            genders[0] -> userPatchData.sex = SEX.M
            genders[1] -> userPatchData.sex = SEX.F
            else -> userPatchData.sex = SEX.NA
        }

        /** Activity Level  */
        userPatchData.activity_level = activityLevel

        /** Weight  */
        userPatchData.weight =  binding.weightEt.text.toString().toFloat()

        /** Height  */
        userPatchData.height = binding.heightEt.text.toString().toFloat()

        return userPatchData
    }


    fun validation(): Boolean {
        var isValid = true

        /** Sex  */
        val sexString = binding.heightEt.text.toString()
        if(sexString.isEmpty()) {
            binding.sexTL.isErrorEnabled=true
            binding.sexTL.error=getString(R.string.invalid_sex)
            isValid = false
        }
        else
            binding.sexTL.isErrorEnabled=false

        /** Height  */
        val heightString = binding.heightEt.text.toString()
        val height = heightString.toFloatOrNull()

        when {
            heightString.isBlank() -> {
                binding.heightTL.isErrorEnabled = true
                binding.heightTL.error = getString(R.string.enter_height)
            }
            height == null || (height !in 120.0..300.0 && height !in 1.20..3.0) -> {
                binding.heightTL.isErrorEnabled = true
                binding.heightTL.error = getString(R.string.height_problem_2)
            }
            else -> {
                if (height in 1.20..3.0)
                    binding.heightEt.setText((height * 100).toString())

                binding.heightTL.isErrorEnabled = false
            }
        }

        val weightString = binding.weightEt.text.toString()
        val weight = weightString.toFloatOrNull()

        /** Weight  */
        when {
            weightString.isBlank() -> {
                binding.weightTL.isErrorEnabled = true
                binding.weightTL.error = getString(R.string.enter_weight)
                isValid = false
            }
            weight == null || weight !in 40F..150F -> {
                binding.weightTL.isErrorEnabled = true
                binding.weightTL.error = getString(R.string.weight_problem_1)
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
            binding.errorActivityLevelTV.text=getString(R.string.enter_activity_level)
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


    private fun bindObservers() {

        userViewModel.userUpdateResponseLiveData.observe(viewLifecycleOwner) { userSessionResponse ->
            userSessionResponse.getContentIfNotHandled()?.let {

                when (it) {
                    is NetworkResult.Success -> {
                        toast("Dados atualizados com sucesso")
                        findNavController().navigateUp()

                    }
                    is NetworkResult.Error -> {
                        toast("Dados não atualizados, alguma coisa se passou.")
                    }
                    is NetworkResult.Loading -> {
                        // show loading bar
                        //todo falta aqui uma loading bar

                    }
                }
            }
        }
    }

}