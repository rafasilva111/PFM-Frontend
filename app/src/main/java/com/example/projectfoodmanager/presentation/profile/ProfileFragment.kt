package com.example.projectfoodmanager.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentProfileBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.toast

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    val authViewModel: AuthViewModel by viewModels()
    val TAG: String = "ProfileFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.logoutIB.setOnClickListener {
            authViewModel.logout()
        }

        binding.favoritesCV.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_favorites)
        }

        binding.likeCV.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_favorites,Bundle().apply {
                putString("aba","gostos")
            })
        }
    }

    fun observer(){
        authViewModel.logout.observe(viewLifecycleOwner) { successful ->
            if (successful == true){
                toast(getString(R.string.logout_completed))
                authViewModel.navigateToPage()
                findNavController().navigateUp()

            }else if(successful == false){
                toast(authViewModel.error.value)
                authViewModel.navigateToPage()
            }
        }
    }

}