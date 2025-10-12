package com.example.projectfoodmanager.presentation.goals.createGoal.tabs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentCreateGoalBinding
import com.example.projectfoodmanager.databinding.FragmentGoalCreateGoalWeightBinding
import com.example.projectfoodmanager.databinding.FragmentGoalFatBinding
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.fitnessReport
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.goalGenericReport
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.userGoal
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.GoalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GoalFatFragment(private var parentBinding: FragmentCreateGoalBinding) : Fragment() {




    /** binding */
    private lateinit var binding: FragmentGoalFatBinding

    /** viewModels */

    /** variables */
    private val TAG: String = "GoalFatFragment"

    private var goal: Float = -1.0F


    /** injects */


    /** adapters */



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentGoalFatBinding.inflate(layoutInflater)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {

        /**
         *  General
         * */

        binding.sevenPercValue.text = "${goalGenericReport.fat.saturatedFatSeven} g"
        binding.tenPercValue.text = "${goalGenericReport.fat.saturatedFatTen} g"

        super.onResume()
    }

    private fun setUI() {

        /**
         *  General
         * */



        /**
         *  Navigation
         * */

        binding.backBtn.setOnClickListener {

            // Slide to previous page
            parentBinding.fragmentRegisterViewPager.currentItem= parentBinding.fragmentRegisterTabLayout.selectedTabPosition- 1

        }

        binding.continueBtn.setOnClickListener {
            if (validation()){

                // Slide to next page
                parentBinding.fragmentRegisterViewPager.currentItem= parentBinding.fragmentRegisterTabLayout.selectedTabPosition +1
            }
        }

        /**
         *  Validations
         * */


        binding.goalRg.setOnCheckedChangeListener { _, checkedId ->
            validateCalories()

            /** Fill user goal fat */
            userGoal.fatLowerLimit = goalGenericReport.fat.fatTwentyThirty.lowerLimit
            userGoal.fatUpperLimit = goalGenericReport.fat.fatTwentyThirty.upperLimit

            when(checkedId){
                R.id.seven_perc-> userGoal.saturatedFat = goalGenericReport.fat.saturatedFatSeven
                R.id.ten_perc-> userGoal.saturatedFat = goalGenericReport.fat.saturatedFatTen
            }
        }


    }

    private fun validation(): Boolean {

        var isValid = true

        /** Height  */

        isValid = validateCalories() and isValid

        return isValid
    }

    private fun validateCalories():Boolean{

        var isValid = true
        if (binding.goalRg.checkedRadioButtonId == -1) {
            isValid = false

            binding.sevenPerc.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.sevenPerc.setTextColor(resources.getColor(R.color.red,null))
            binding.tenPerc.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.tenPerc.setTextColor(resources.getColor(R.color.red,null))

            binding.errorFatTV.visibility=View.VISIBLE
            binding.errorFatTV.text="You need to chose an option"
        }else{
            binding.sevenPerc.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.sevenPerc.setTextColor(resources.getColor(R.color.black,null))
            binding.tenPerc.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.tenPerc.setTextColor(resources.getColor(R.color.black,null))

            binding.errorFatTV.visibility=View.INVISIBLE
        }
        return isValid
    }

}