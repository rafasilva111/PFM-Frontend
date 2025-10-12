package com.example.projectfoodmanager.presentation.goals

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentGoalsBinding
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.GoalsViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GoalsFragment : Fragment() {

    /** binding */
    private lateinit var binding: FragmentGoalsBinding

    /** viewModels */
    private val goalsViewModel: GoalsViewModel by viewModels()

    /** variables */
    private val TAG: String = "GoalsFragment"

    private lateinit  var  checkForBiodataDialog: MaterialAlertDialogBuilder
    private lateinit var checkForGoalsDialog: MaterialAlertDialogBuilder

    /** injects */
    @Inject
    lateinit var sharedPreference: SharedPreference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentGoalsBinding.inflate(layoutInflater)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    override fun onStart() {
        loadUI()
        super.onStart()
    }

    private fun loadUI() {

        /**
         *  Load Variables
         *
         *  Note:
         *      This Should be loaded as soon as possible in loadUI
         *
         * */

        val user = sharedPreference.getUserSession()

        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeTheme(false, activity, requireContext())


        /** Check for Bio-data */
        if (user.sex == null || user.weight < 0.0 && user.height < 0.0 && user.activityLevel < 1.0)
            checkForBiodataDialog.show()

        /** Check for Goals */
        else if (user.fitnessGoal == null)
            checkForGoalsDialog.show()


    }

    private fun setUI() {



        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeTheme(true, activity, requireContext())


        /** Check for Biodata Dialog */

        checkForBiodataDialog = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_logout) // TODO RUI escolher um icon para aqui
            .setCancelable(false)
            .setTitle(getString(R.string.goals_biodata_check_dialog_title))
            .setMessage(getString(R.string.goals_biodata_check_dialog_desc))
            .setPositiveButton(getString(R.string.COMMON_DIALOG_YES)) { dialog, _ ->
                findNavController().navigate(R.id.action_goalsFragment_to_updateBiodata)

                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.COMMON_DIALOG_NO)) { dialog, _ ->
                findNavController().navigateUp()
                dialog.dismiss()
            }



        /** Check for Goals Dialog */

        checkForGoalsDialog = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_logout) // TODO RUI escolher um icon para aqui
            .setCancelable(false)
            .setTitle(getString(R.string.goals_check_dialog_title))
            .setMessage(getString(R.string.goals_check_dialog_desc))
            .setPositiveButton(getString(R.string.COMMON_DIALOG_YES)) { dialog, _ ->
                findNavController().navigate(R.id.action_goalsFragment_to_createGoalFragment)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.COMMON_DIALOG_NO)) { dialog, _ ->
                findNavController().navigateUp()
                dialog.dismiss()
            }


    }


    private fun bindObservers() {
    }
}