package com.example.projectfoodmanager.presentation.onboarding.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentFirstScreenBinding
import com.example.projectfoodmanager.databinding.FragmentHomeBinding

class FirstScreen : Fragment() {

    lateinit var binding: FragmentFirstScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFirstScreenBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager2)

        binding.next1.setOnClickListener {
            viewPager?.currentItem =1
        }
    }
}