package com.example.projectfoodmanager.presentation.profile.settings.security

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.databinding.FragmentSecurityBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecurityFragment : Fragment() {

    lateinit var binding: FragmentSecurityBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindObservers()

        return if (this::binding.isInitialized) {
            binding.root
        } else {
            // Inflate the layout for this fragment
            binding = FragmentSecurityBinding.inflate(layoutInflater)

            binding.root
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun bindObservers() {}

}