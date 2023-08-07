package com.example.projectfoodmanager.presentation.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentFollowRequestBinding
import com.example.projectfoodmanager.databinding.FragmentFollowerBinding
import com.example.projectfoodmanager.databinding.FragmentNotificationBinding
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding


class NotificationFragment : Fragment() {

    // binding
    lateinit var binding: FragmentNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentNotificationBinding.inflate(layoutInflater)

           // binding.followerRV.layoutManager = LinearLayoutManager(activity)
           // binding.followerRV.adapter = adapter

            bindObservers()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.requestFollowCV.setOnClickListener {

            findNavController().navigate(R.id.action_notificationFragment_to_followRequestFragment)

        }
    }

    private fun bindObservers() {
        //TODO("Not yet implemented")
    }

}