package com.example.projectfoodmanager

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentHomeNewBinding
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.enableEdgeToEdge
import com.example.projectfoodmanager.util.Helper.Companion.restoreViewLimits

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

    override fun onStart() {
        super.onStart()
        loadUI()
    }

    private fun setUI() {

        /**
         *  General
         * */

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

    private fun loadUI(){
        /** Remove Status Bar and Navigation Bar view limits */
        enableEdgeToEdge(requireActivity(), requireView())

    }

}