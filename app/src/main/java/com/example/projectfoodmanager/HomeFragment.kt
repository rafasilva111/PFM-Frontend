package com.example.projectfoodmanager

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentHomeNewBinding

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeNewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeNewBinding.inflate(layoutInflater)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            requireActivity().window.navigationBarColor = Color.TRANSPARENT
            requireActivity().window.statusBarColor = Color.TRANSPARENT
            requireActivity().window.decorView.systemUiVisibility = 0
        }else {
            requireActivity().window.navigationBarColor = requireContext().getColor(R.color.main_color)
            requireActivity().window.statusBarColor =  requireContext().getColor(R.color.main_color)
            requireActivity().window.decorView.systemUiVisibility = 0
        }

/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            requireActivity().window.navigationBarColor = Color.TRANSPARENT
            requireActivity().window.statusBarColor = Color.TRANSPARENT
            requireActivity().window.decorView.systemUiVisibility = 0
        }else {
            requireActivity().window.navigationBarColor = requireContext().getColor(R.color.main_color)
            requireActivity().window.statusBarColor =  requireContext().getColor(R.color.main_color)
            requireActivity().window.decorView.systemUiVisibility = 8192
            requireActivity().window.statusBarColor =  requireContext().getColor(R.color.main_color)

        }*/

  /*      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller =  requireActivity().window.insetsController
            if (controller != null) {
                controller.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            }
        } else {
            val flags =
                requireActivity().window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            requireActivity().window.decorView.systemUiVisibility = flags
        }*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginBTN.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.registerBTN.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registerFragment)
        }

    }

    override fun onPause() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val window = requireActivity().window
            window.decorView.systemUiVisibility = 0
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        }

        requireActivity().window.navigationBarColor = Color.TRANSPARENT
        requireActivity().window.statusBarColor = Color.TRANSPARENT
        super.onPause()
    }


}