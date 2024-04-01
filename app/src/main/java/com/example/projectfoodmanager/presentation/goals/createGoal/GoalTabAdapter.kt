package com.example.projectfoodmanager.presentation.goals.createGoal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectfoodmanager.databinding.FragmentCreateGoalBinding
import com.example.projectfoodmanager.presentation.goals.createGoal.tabs.GoalCaloriesFragment
import com.example.projectfoodmanager.presentation.goals.createGoal.tabs.GoalCarbohydratesFragment
import com.example.projectfoodmanager.presentation.goals.createGoal.tabs.GoalFatFragment
import com.example.projectfoodmanager.presentation.goals.createGoal.tabs.GoalOverviewFragment

class GoalTabAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val parentBinding: FragmentCreateGoalBinding,
    ): FragmentStateAdapter(fragmentManager,lifecycle) {


    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> GoalCaloriesFragment(parentBinding)
            1-> GoalCarbohydratesFragment(parentBinding)
            2-> GoalFatFragment(parentBinding)
            else-> GoalOverviewFragment(parentBinding)
        }
    }


}