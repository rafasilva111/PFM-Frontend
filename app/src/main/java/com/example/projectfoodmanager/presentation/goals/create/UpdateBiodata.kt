package com.example.projectfoodmanager.presentation.goals.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.databinding.FragmentUpdateBiodataBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpdateBiodata : Fragment() {
    lateinit var binding: FragmentUpdateBiodataBinding
    val userViewModel: UserViewModel by viewModels()
    val TAG: String = "ProfileFragment"
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
        bindObservers()
        super.onViewCreated(view, savedInstanceState)

    }




    private fun setUI() {
        /**
         *  General
         * */

        val activity = requireActivity()
        Helper.changeMenuVisibility(false, activity)
        Helper.changeStatusBarColor(false, activity, requireContext())

        /**
         *  Navigation
         * */

        binding.backIB.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle(getString(R.string.warning))
                .setMessage(getString(R.string.onBackWarning))
                .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                    /** Sends you to update ure bio-data */
                    findNavController().navigateUp()
                }
                .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
                    /** Go back */
                    dialog.dismiss()
                }
                .show()
        }

        binding.updateBtn.setOnClickListener {
            if (validation()) {
                userViewModel.updateUser(getUserRequest())
            }
        }

    }

    private fun getUserRequest(): UserDTO {
        var activityLevel = 0.0F
        when(binding.activityLevelRg.checkedRadioButtonId){
            R.id.op1_RB-> activityLevel= 1.2F
            R.id.op2_RB-> activityLevel= 1.375F
            R.id.op3_RB-> activityLevel= 1.465F
            R.id.op4_RB-> activityLevel= 1.55F
            R.id.op5_RB-> activityLevel= 1.725F
            R.id.op6_RB-> activityLevel= 1.9F
        }
        return UserDTO(
            height = binding.heightEt.text.toString().toFloat(),
            weight = binding.weightEt.text.toString().toFloat(),
            activity_level = activityLevel,
            )
    }

    private fun validation(): Boolean {
        var isValid = true

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
            userSessionResponse.getContentIfNotHandled()?.let { result ->

                when (result) {
                    is NetworkResult.Success -> {
                        getString(R.string.biodata_update_successfully)
                        findNavController().navigateUp()

                    }
                    is NetworkResult.Error -> {
                        getString(R.string.biodata_update_unsuccessfully)
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