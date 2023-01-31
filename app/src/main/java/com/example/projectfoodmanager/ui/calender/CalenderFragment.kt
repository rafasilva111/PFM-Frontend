package com.example.projectfoodmanager.ui.calender

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.LoginActivity
import com.example.projectfoodmanager.databinding.FragmentCalenderBinding
import com.example.projectfoodmanager.databinding.FragmentProfileBinding

import com.example.projectfoodmanager.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CalenderFragment : Fragment() {
    lateinit var binding: FragmentCalenderBinding
    val authViewModel: AuthViewModel by viewModels()
    val TAG: String = "ProfileFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {
        binding = FragmentCalenderBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}