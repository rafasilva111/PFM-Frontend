package com.example.projectfoodmanager.presentation.profile.settings.aboutUs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentAboutUsBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AboutUsFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentAboutUsBinding

    // viewModels

    // constants
    private val TAG: String = "AboutUsFragment"

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentAboutUsBinding.inflate(layoutInflater)



        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        bindObservers()
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