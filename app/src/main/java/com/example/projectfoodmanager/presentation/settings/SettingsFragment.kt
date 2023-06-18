package com.example.projectfoodmanager.presentation.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentProfileBinding
import com.example.projectfoodmanager.databinding.FragmentSettingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsFragment : Fragment() {

    lateinit var binding: FragmentSettingsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun changeVisib_Menu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

    override fun onResume() {
        requireActivity().window.decorView.systemUiVisibility = 0
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.main_color)
        changeVisib_Menu(false)

        super.onResume()
    }

    override fun onPause() {
        requireActivity().window.decorView.systemUiVisibility = 8192
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.background_1)
        changeVisib_Menu(true)

        super.onPause()
    }
}