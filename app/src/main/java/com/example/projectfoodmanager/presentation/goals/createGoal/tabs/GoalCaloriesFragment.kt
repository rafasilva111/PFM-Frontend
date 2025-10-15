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
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.fitnessReport
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.goalGenericReport
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.userGoal
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.viewmodels.GoalsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GoalCaloriesFragment(private var parentBinding: FragmentCreateGoalBinding) : Fragment() {




    /** binding */
    private lateinit var binding: FragmentGoalCreateGoalWeightBinding

    /** viewModels */
    private val goalsViewModel: GoalsViewModel by viewModels()

    /** variables */
    private val TAG: String = "GoalWeightFragment"



    /** injects */

    /** adapters */

    /** observers */



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentGoalCreateGoalWeightBinding.inflate(layoutInflater)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        bindObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        bindObservers()
        loadUI()
        super.onStart()
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

                // Slide to next page
                parentBinding.fragmentRegisterViewPager.currentItem= parentBinding.fragmentRegisterTabLayout.selectedTabPosition +1

            }
        }

        /**
         *  Validations
         * */


        binding.goalRg.setOnCheckedChangeListener { _, checkedId ->
            validateCalories()

            /** Fill user goal proteins */
            userGoal.proteinsLowerLimit = fitnessReport.protein.lowerLimit
            userGoal.proteinsUpperLimit = fitnessReport.protein.upperLimit


            when(checkedId){
                R.id.maintain_weight-> {

                    /** Choose generic report*/
                    goalGenericReport = fitnessReport.maintain

                    /** Fill user goal */
                    userGoal.goal = 0F
                    userGoal.calories = goalGenericReport.calories!!.toFloat()
                }
                R.id.gain_1-> {

                    /** Choose generic report*/
                    goalGenericReport = fitnessReport.plus

                    /** Fill user goal */
                    userGoal.goal = 1F
                    userGoal.calories = goalGenericReport.calories!!.toFloat()
                }
                R.id.gain_0_5-> {

                    /** Choose generic report*/
                    goalGenericReport = fitnessReport.plusHalf

                    /** Fill user goal */
                    userGoal.goal = 0.5F
                    userGoal.calories = goalGenericReport.calories!!.toFloat()
                }
                R.id.lose_1-> {

                    /** Choose generic report*/
                    goalGenericReport = fitnessReport.minus

                    /** Fill user goal */
                    userGoal.goal = -0.5F
                    userGoal.calories = goalGenericReport.calories!!.toFloat()
                }
                R.id.lose_0_5-> {

                    /** Choose generic report*/
                    goalGenericReport = fitnessReport.minusHalf

                    /** Fill user goal */
                    userGoal.goal = -1F
                    userGoal.calories = goalGenericReport.calories!!.toFloat()
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

        goalsViewModel.getFitnessModel()


        /**
         *  General
         * */


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

            binding.gain1.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.gain1.setTextColor(resources.getColor(R.color.red,null))
            binding.gain05.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.gain05.setTextColor(resources.getColor(R.color.red,null))
            binding.lose05.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.lose05.setTextColor(resources.getColor(R.color.red,null))
            binding.lose1.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.lose1.setTextColor(resources.getColor(R.color.red,null))
            binding.maintainWeight.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.maintainWeight.setTextColor(resources.getColor(R.color.red,null))

            binding.errorActivityLevelTV.visibility=View.VISIBLE
            binding.errorActivityLevelTV.text="You need to chose an option"
        }else{
            binding.gain1.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.gain1.setTextColor(resources.getColor(R.color.black,null))
            binding.gain05.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.gain05.setTextColor(resources.getColor(R.color.black,null))
            binding.lose05.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.lose05.setTextColor(resources.getColor(R.color.black,null))
            binding.lose1.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.lose1.setTextColor(resources.getColor(R.color.black,null))
            binding.maintainWeight.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.maintainWeight.setTextColor(resources.getColor(R.color.black,null))
            binding.errorActivityLevelTV.visibility=View.INVISIBLE
        }
        return isValid
    }


    private fun bindObservers() {

        /**
         * Ideal Weight
         */

        goalsViewModel.getFitnessModelLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        fitnessReport = it.data!!

                        binding.minWeightRecommendedValue.text = fitnessReport.idealWeight.lowerLimit.toString() + " Kg"
                        binding.maxWeightRecommendedValue.text = fitnessReport.idealWeight.upperLimit.toString() + " Kg"

                        binding.maintainWeightValue.text = "${fitnessReport.maintain.calories.toString()} Calories"
                        binding.gain05Value.text = fitnessReport.plusHalf.calories.toString() + " Calories"
                        binding.gain1Value.text = fitnessReport.plus.calories.toString() + " Calories"

                        binding.lose05Value.text = fitnessReport.minusHalf.calories.toString() + " Calories"

                        if (fitnessReport.minus.calories == null)
                            binding.lose1.isEnabled = false
                        else
                            binding.lose1Value.text = fitnessReport.minus.calories.toString() + " Calories"


                        teste()
                    }
                    is NetworkResult.Error -> {
                        // Handle error if needed
                    }
                    is NetworkResult.Loading -> {
                        // Handle loading state if needed
                    }
                }
            }
        }
    }

    private fun teste(){
        binding.fragmentGoalCreateGoalWeightPb.visibility = View.GONE
        binding.fragmentGoalCreateGoalWeightParentCl.visibility = View.VISIBLE
    }

}