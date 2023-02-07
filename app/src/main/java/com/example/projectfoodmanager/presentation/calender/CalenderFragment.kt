package com.example.projectfoodmanager.presentation.calender

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.databinding.FragmentCalenderBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel

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