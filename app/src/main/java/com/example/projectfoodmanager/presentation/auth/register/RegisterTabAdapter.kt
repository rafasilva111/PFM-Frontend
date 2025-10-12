package com.example.projectfoodmanager.presentation.auth.register

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.presentation.auth.register.tabs.AccountAuthFragment
import com.example.projectfoodmanager.presentation.auth.register.tabs.AccountProfileFragment
import com.example.projectfoodmanager.presentation.auth.register.tabs.AccountBioDataFragment

class RegisterTabAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val parentBinding: FragmentRegisterBinding,
    ): FragmentStateAdapter(fragmentManager,lifecycle) {


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {


        return when(position){
            0-> AccountProfileFragment(parentBinding)
            1-> AccountAuthFragment(parentBinding)
            else-> AccountBioDataFragment(parentBinding)
        }
    }


}