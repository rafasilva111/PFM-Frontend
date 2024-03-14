package com.example.projectfoodmanager.presentation.goals.createGoal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectfoodmanager.databinding.FragmentCreateGoalBinding
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.presentation.auth.register.tabs.AccountAuthFragment
import com.example.projectfoodmanager.presentation.auth.register.tabs.AccountProfileFragment
import com.example.projectfoodmanager.presentation.auth.register.tabs.AccountBioDataFragment
import com.example.projectfoodmanager.presentation.goals.createGoal.tabs.GoalWeightFragment

class GoalTabAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val parentBinding: FragmentCreateGoalBinding,
    ): FragmentStateAdapter(fragmentManager,lifecycle) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {


        return when(position){
            else-> GoalWeightFragment(parentBinding)
        }
    }


}