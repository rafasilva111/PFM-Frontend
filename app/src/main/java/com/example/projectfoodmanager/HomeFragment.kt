package com.example.projectfoodmanager

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentHomeNewBinding
import com.example.projectfoodmanager.presentation.recipe.details.RecipeDetailFragment
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor

class HomeFragment : Fragment() {

    // binding
    lateinit var binding: FragmentHomeNewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeNewBinding.inflate(layoutInflater)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setUI() {

        /**
         *  General
         * */

        
        changeStatusBarColor(true, requireActivity(), requireContext())

        /** no status bar limits */

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        /**
         *  Navigation
         * */

        binding.loginBTN.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.registerBTN.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registerFragment)
        }
    }

    override fun onPause() {
        super.onPause()

        @Suppress("DEPRECATION")
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }


}