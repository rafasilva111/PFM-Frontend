package com.example.projectfoodmanager.presentation.profile.settings.security

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentFaqsBinding
import com.example.projectfoodmanager.databinding.FragmentSecurityBinding
import com.example.projectfoodmanager.util.Helper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecurityFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentSecurityBinding

    // viewModels

    // constants
    private val TAG: String = "SecurityFragment"

    // injects


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
        setUI()
        bindObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUI() {

        /**
         * General
         */

        val activity = requireActivity()

        Helper.changeMenuVisibility(false, activity)
        Helper.changeStatusBarColor(false, activity, requireContext())

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun bindObservers() {

    }

}