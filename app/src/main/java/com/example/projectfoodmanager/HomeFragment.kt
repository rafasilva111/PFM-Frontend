package com.example.projectfoodmanager

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentHomeBinding
import com.example.projectfoodmanager.databinding.FragmentHomeNewBinding
import com.example.projectfoodmanager.databinding.FragmentLoginBinding
import com.example.projectfoodmanager.util.ToastConstants
import com.example.projectfoodmanager.util.hide
import com.example.projectfoodmanager.util.showCustomToast

class HomeFragment : Fragment() {

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
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registerFragment)
        }

    }
}