package com.example.projectfoodmanager.presentation.goals.create

import PopUpFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.databinding.FragmentCreateGoalBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CreateGoalFragment : Fragment() {


    /** binding */
    private lateinit var binding: FragmentCreateGoalBinding

    /** viewModels */

    /** variables */
    private val TAG: String = "CreateGoalFragment"


    /** injects */
    @Inject
    lateinit var sharedPreference: SharedPreference

    /** adapters */




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentCreateGoalBinding.inflate(layoutInflater)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    private fun setUI() {
        /**
         *  General
         * */

        val activity = requireActivity()
        Helper.changeMenuVisibility(false, activity)
        Helper.changeStatusBarColor(true, activity, requireContext())


    }

    private fun bindObservers() {

    }

    override fun onPause() {
        // usar para atualizar/destruir listas de elementos
        super.onPause()
    }



}