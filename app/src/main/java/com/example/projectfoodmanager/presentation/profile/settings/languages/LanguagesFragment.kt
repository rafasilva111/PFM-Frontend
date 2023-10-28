package com.example.projectfoodmanager.presentation.profile.settings.languages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.projectfoodmanager.databinding.FragmentLanguagesBinding
import com.example.projectfoodmanager.util.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LanguagesFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentLanguagesBinding

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
        binding = FragmentLanguagesBinding.inflate(layoutInflater)



        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        bindObservers()
    }

    private fun setUI() {

    }

    private fun bindObservers() {

    }


}