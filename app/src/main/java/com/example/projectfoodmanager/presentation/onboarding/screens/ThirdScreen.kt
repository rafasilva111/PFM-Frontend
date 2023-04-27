package com.example.projectfoodmanager.presentation.onboarding.screens

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentHomeBinding
import com.example.projectfoodmanager.databinding.FragmentThirdScreenBinding


class ThirdScreen : Fragment() {

    lateinit var binding: FragmentThirdScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentThirdScreenBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.BTNFinish.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
            onBoardingFinished()
        }
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished",true)
        editor.apply()
    }

}