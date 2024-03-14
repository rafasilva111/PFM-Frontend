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
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.userGoal
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.GoalsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GoalWeightFragment(private var parentBinding: FragmentCreateGoalBinding) : Fragment() {




    /** binding */
    private lateinit var binding: FragmentGoalCreateGoalWeightBinding

    /** viewModels */
    private val goalsViewModel: GoalsViewModel by viewModels()

    /** variables */
    private val TAG: String = "GoalWeightFragment"

    private var goal: Float = -1.0F


    /** injects */

    @Inject
    lateinit var sharedPreference: SharedPreference

    /** adapters */



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
                patchGoal()
                val currentTab = parentBinding.fragmentRegisterTabLayout.selectedTabPosition
                val nextTab = currentTab + 1
                parentBinding.fragmentRegisterViewPager.currentItem= nextTab

            }
        }

        /**
         *  Validations
         * */


        binding.goalRg.setOnCheckedChangeListener { _, checkedId ->
            validateCalories()
            when(checkedId){
                R.id.maintain_weight-> goal= 0F
                R.id.gain_1-> goal= 1F
                R.id.gain_0_5-> goal= 0.5F
                R.id.lose_1-> goal= -0.5F
                R.id.lose_0_5-> goal= -1F
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
            binding.errorActivityLevelTV.text=getString(R.string.enter_activity_level)
        }else{
            binding.gain1.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.gain1.setTextColor(resources.getColor(R.color.black,null))
            binding.gain05.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.gain05.setTextColor(resources.getColor(R.color.black,null))
            binding.lose05.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.lose05.setTextColor(resources.getColor(R.color.black,null))
            binding.lose1.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.lose1.setTextColor(resources.getColor(R.color.black,null))
            binding.maintainWeight.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.maintainWeight.setTextColor(resources.getColor(R.color.black,null))
            binding.errorActivityLevelTV.visibility=View.INVISIBLE
        }
        return isValid
    }


    private fun patchGoal() {

        /** Goal  */

        userGoal.goal = goal

    }

    private fun bindObservers() {

        /**
         * Ideal Weight
         */

        goalsViewModel.getFitnessModelLiveData.observe(viewLifecycleOwner
        ) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        fitnessReport = it.data!!

                        binding.minWeightRecommendedValue.text = fitnessReport.idealWeight.lowerLimit.toString() +" Kg"
                        binding.maxWeightRecommendedValue.text = fitnessReport.idealWeight.upperLimit.toString()+" Kg"

                        binding.maintainWeightValue.text = "${fitnessReport.maintain.calories.toString()} Calories"
                        binding.gain05Value.text = fitnessReport.plusHalf.calories.toString()+" Calories"
                        binding.gain1Value.text =  fitnessReport.plus.calories.toString()+" Calories"

                        binding.lose05Value.text =  fitnessReport.minusHalf.calories.toString()+" Calories"


                        /**
                         * Deal whit null calories ( when calories are null it means option is not recommended )
                         */

                        if (fitnessReport.minus.calories == null)
                            binding.lose1.isEnabled = false
                        else
                            binding.lose1Value.text =  fitnessReport.minus.calories.toString()+" Calories"

                        /**
                         * Hide progress bar
                         */
                        binding.fragmentGoalCreateGoalWeightPb.visibility = View.GONE

                        /**
                         * Show Main view
                         */
                        binding.fragmentGoalCreateGoalWeightParentCl.visibility = View.VISIBLE
                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }
    }

}