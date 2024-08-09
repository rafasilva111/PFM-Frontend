package com.example.projectfoodmanager.presentation.profile.settings.languages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentLanguagesBinding
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
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

        // todo rui fazer recycler view para selecionar a data
    }

    private fun setUI() {



        /**
         * General
         */

        val activity = requireActivity()

        changeMenuVisibility(false,activity)
        changeTheme(false,activity,requireContext())

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }




    }

    private fun bindObservers() {

    }


}