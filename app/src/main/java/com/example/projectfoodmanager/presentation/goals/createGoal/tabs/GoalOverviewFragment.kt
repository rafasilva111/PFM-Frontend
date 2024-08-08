package com.example.projectfoodmanager.presentation.goals.createGoal.tabs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentCreateGoalBinding
import com.example.projectfoodmanager.databinding.FragmentGoalOverviewBinding
import com.example.projectfoodmanager.presentation.goals.createGoal.CreateGoalFragment.Companion.userGoal
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.viewmodels.GoalsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GoalOverviewFragment(private var parentBinding: FragmentCreateGoalBinding) : Fragment() {




    /** binding */
    private lateinit var binding: FragmentGoalOverviewBinding

    /** viewModels */
    private val goalsViewModel: GoalsViewModel by viewModels()

    /** variables */
    private val TAG: String = "GoalOverviewFragment"



    /** injects */


    /** adapters */



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentGoalOverviewBinding.inflate(layoutInflater)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        bindObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        loadUI()
        super.onResume()
    }

    private fun loadUI() {

        /**
         *  General
         * */

        /** Goal */

        binding.goalTitle.text = "Objectivo: ${userGoal.goal.toString()} Kg"

        /** Calories */

        binding.caloriesValue.text = "${userGoal.calories.toString()} cal"

        /** Fat */

        binding.fatValue.text = "${userGoal.fatLowerLimit.toString()} g - ${userGoal.fatUpperLimit.toString()} g"

        /** Saturated Fat */

        binding.saturatedFatValue.text = "${userGoal.saturatedFat.toString()} g"

        /** Carbohydrates */

        binding.carbohydratesValue.text = "${userGoal.carbohydrates.toString()} g"

        /** Proteins */

        binding.proteinValue.text = "${userGoal.proteinsLowerLimit.toString()} g - ${userGoal.proteinsUpperLimit.toString()} g"
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
            goalsViewModel.createFitnessGoal(goalDTO = userGoal)
        }

    }

    private fun bindObservers() {

        /**
         * Ideal Weight
         */

        goalsViewModel.createFitnessGoalLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Success")

                        findNavController().navigateUp()
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



}