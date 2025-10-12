package com.example.projectfoodmanager.presentation.profile.settings.faqs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentFaqsBinding
import com.example.projectfoodmanager.util.Helper
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FAQsFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentFaqsBinding

    // viewModels

    // constants
    private val TAG: String = "FAQsFragment"

    // injects




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentFaqsBinding.inflate(layoutInflater)



        return binding.root
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
        Helper.changeTheme(false, activity, requireContext())

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun bindObservers() {

    }


}