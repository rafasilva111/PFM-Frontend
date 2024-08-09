package com.example.projectfoodmanager.presentation.goals

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.user.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentUpdateBiodataBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Constraints.USER_MAX_HEIGHT
import com.example.projectfoodmanager.util.Constraints.USER_MAX_WEIGHT
import com.example.projectfoodmanager.util.Constraints.USER_MIN_HEIGHT
import com.example.projectfoodmanager.util.Constraints.USER_MIN_WEIGHT
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
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
    private var activityLevel : Double = 0.0

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
            .setPositiveButton(getString(R.string.COMMON_DIALOG_YES)) { dialog, _ ->
                findNavController().navigateUp()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.COMMON_DIALOG_NO)) { dialog, _ ->
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
                validateSex(sexString = binding.sexEt.text.toString())

            }else{
                val imm = binding.sexTL.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        /** Activity Level  */
        binding.activityLevelRg.setOnCheckedChangeListener { _, checkedId ->
            validateActivityLevel()
            when (checkedId) {
                R.id.op1_RB -> activityLevel = 1.2
                R.id.op2_RB -> activityLevel = 1.375
                R.id.op3_RB -> activityLevel = 1.465
                R.id.op4_RB -> activityLevel = 1.55
                R.id.op5_RB -> activityLevel = 1.725
                R.id.op6_RB -> activityLevel = 1.9
            }

        }

        /** Weight  */
        binding.weightEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val weightString = binding.weightEt.text.toString()
                val weight = weightString.toFloatOrNull()
                validateWeight(weightString,weight)
            }
        }

        /** Height  */
        binding.heightEt.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus) {
                val heightString = binding.heightEt.text.toString()
                val height = heightString.toFloatOrNull()

                validateHeight(heightString,height)
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
        changeTheme(true, activity, requireContext())



        /** Biometric Data  */

        val genders = resources.getStringArray(R.array.gender_array)
        when(user.sex){
            SEX.M -> binding.sexEt.setText(genders[0], false)
            SEX.F-> binding.sexEt.setText(genders[1], false)
        }

        binding.heightEt.setText(user.height.toInt().toString())
        binding.weightEt.setText(user.weight.toInt().toString())

        when(user.activityLevel.toFloat()){
            1.2F -> binding.activityLevelRg.check(R.id.op1_RB)
            1.375F -> binding.activityLevelRg.check(R.id.op2_RB)
            1.465F -> binding.activityLevelRg.check(R.id.op3_RB)
            1.55F -> binding.activityLevelRg.check(R.id.op4_RB)
            1.725F -> binding.activityLevelRg.check(R.id.op5_RB)
            1.9F -> binding.activityLevelRg.check(R.id.op6_RB)
        }

    }

    private fun patchUser(): UserRequest {
        val userPatchData = UserRequest()

        /** Sex  */
        val genders = resources.getStringArray(R.array.gender_array)
        when(binding.sexEt.text.toString()){
            genders[0] -> userPatchData.sex = SEX.M
            genders[1] -> userPatchData.sex = SEX.F
        }

        /** Activity Level  */
        userPatchData.activityLevel = activityLevel

        /** Weight  */
        userPatchData.weight =  binding.weightEt.text.toString().toFloat()

        /** Height  */
        userPatchData.height = binding.heightEt.text.toString().toFloat()

        return userPatchData
    }

    fun validation(): Boolean {

        var isValid = true

        /** Sex  */

        isValid = isValid and validateSex(sexString = binding.sexEt.text.toString())


        /** Height  */
        val heightString = binding.heightEt.text.toString()
        val height = heightString.toFloatOrNull()

        isValid = isValid and validateHeight(heightString,height)


        /** Weight  */
        val weightString = binding.weightEt.text.toString()
        val weight = weightString.toFloatOrNull()

        isValid = isValid and validateWeight(weightString,weight)


        /** Activity Level  */
        isValid = isValid and validateActivityLevel()

        return isValid
    }

    private fun validateSex(sexString: String): Boolean {
        return if(sexString.isEmpty()) {
            binding.sexTL.isErrorEnabled=true
            binding.sexTL.error=getString(R.string.USER_ERROR_GENDER_INVALID)
            false
        }
        else{
            binding.sexTL.isErrorEnabled=false
            true
        }

    }

    private fun validateHeight(heightString: String, height: Float?):Boolean {
        return when {
            heightString.isBlank() -> {
                errorOnHeight(getString(R.string.USER_ERROR_HEIGHT_INVALID))
                false
            }
            height == null || (height !in USER_MIN_HEIGHT..USER_MAX_HEIGHT && height !in ((USER_MIN_HEIGHT/100))..((USER_MAX_HEIGHT/100))) -> {
                errorOnHeight(getString(R.string.USER_ERROR_HEIGHT_INVALID_2,USER_MIN_HEIGHT,USER_MAX_HEIGHT))
                false
            }
            else -> {
                if (height in ((USER_MIN_HEIGHT/100))..((USER_MAX_HEIGHT/100)))
                    binding.heightEt.setText((height * 100).toString())
                binding.heightTL.isErrorEnabled = false
                true
            }
        }
    }

    private fun validateWeight(weightString: String, weight: Float?):Boolean {
        return when {
            weightString.isBlank() -> {
                errorOnWeight(getString(R.string.USER_ERROR_WEIGHT_INVALID))
                false
            }
            weight == null || weight !in USER_MIN_WEIGHT..USER_MAX_WEIGHT -> {
                errorOnWeight(getString(R.string.USER_ERROR_HEIGHT_INVALID_2,USER_MIN_WEIGHT,USER_MAX_WEIGHT))
                false
            }
            else -> {
                binding.weightTL.isErrorEnabled = false
                true
            }
        }
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


    private fun bindObservers() {

        userViewModel.userUpdateResponseLiveData.observe(viewLifecycleOwner) { userSessionResponse ->
            userSessionResponse.getContentIfNotHandled()?.let {result->
                when (result) {
                    is NetworkResult.Success -> {
                        toast(getString(R.string.DATA_UPDATED))
                        findNavController().navigateUp()

                    }
                    is NetworkResult.Error -> {
                        toast(getString(R.string.DATA_NOT_UPDATED),ToastType.ALERT)

                        result.error?.let{ it->
                            for (item in it.errors)
                                when(item.key){
                                    Error.ON_WEIGHT -> {
                                        errorOnWeight(item.value[0])
                                    }
                                    Error.ON_HEIGHT -> {
                                        errorOnHeight(item.value[0])
                                    }
                                }
                        }

                    }
                    is NetworkResult.Loading -> {
                        //todo Rui falta aqui uma loading bar
                        //unshow button/show loading bar
                    }
                }
            }
        }
    }

    private fun errorOnWeight(s: String) {
        binding.weightTL.isErrorEnabled=true
        binding.weightTL.error=s
    }

    private fun errorOnHeight(s: String) {
        binding.heightTL.isErrorEnabled=true
        binding.heightTL.error=s
    }

}