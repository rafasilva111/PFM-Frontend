package com.example.projectfoodmanager.presentation.goals

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.databinding.FragmentGoalsBinding
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class GoalsFragment : Fragment() {
    /** binding */
    lateinit var binding: FragmentGoalsBinding

    /** viewModels */
    val userViewModel: UserViewModel by viewModels()

    /** variables */
    private val TAG: String = "ProfileFragment"
    private lateinit var user: User

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



    private fun setUI() {
        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(true, activity)
        changeStatusBarColor(false, activity, requireContext())

        user = sharedPreference.getUserSession()


        /**
         *  Functions
         * */


        if (!validateUserBiodata()){

            /** Ask if user wants to insert his bio-data */

            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle(getString(R.string.biodata_dialog_title))
                .setMessage(getString(R.string.biodata_diolog_desc))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                    /** Sends you to update ure bio-data */
                    findNavController().navigate(R.id.action_goalsFragment_to_updateBiodata)
                }
                .setNegativeButton(getString(R.string.dialog_no)) { _, _ ->
                    /** Go back */
                    findNavController().navigateUp()
                }
                .show()
        }

        if (!validateUserGoals()){

            /** Ask if user wants to insert his goal */

            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle(getString(R.string.goal_dialog_title))
                .setMessage(getString(R.string.goal_dialog_desc))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                    /** Sends you to update ure bio-data */
                    // todo
                    //findNavController().navigate(R.id.action_goalsFragment_to_updateBiodata)
                }
                .setNegativeButton(getString(R.string.dialog_no)) { _, _ ->
                    /** Go back */
                    findNavController().navigateUp()
                }
                .show()
        }



        /**
         *  Insert Bio-data
         * */


        /**
         *  Create Goals
         * */



    }

    private fun validateUserGoals(): Boolean {
        return false
    }

    private fun validateUserBiodata(): Boolean {

        if (user.weight == 0.0 ||
            user.height == 0.0 ||
                user.activity_level == 0.0)
            return false

        return true
    }


    private fun bindObservers() {


        userViewModel.userResponseLiveData.observe(viewLifecycleOwner) { userSessionResponse ->
            userSessionResponse.getContentIfNotHandled()?.let {

                when (it) {
                    is NetworkResult.Success -> {
                        if (!(it.data!!.weight != 0.0 && it.data.height != 0.0 && it.data.activity_level != 1.0)) {
                            val dialogBinding: View = layoutInflater.inflate(R.layout.dialog_goals_biodata_confirmation_from_user, null);

                            val myDialog = Dialog(requireContext())
                            myDialog.setContentView(dialogBinding)

                            // create alert dialog
                            myDialog.setCancelable(false)
                            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                            val yesBtn = dialogBinding.findViewById<Button>(R.id.btn_conf_Yes)
                            val cancelBtn = dialogBinding.findViewById<Button>(R.id.btn_conf_cancel)



                            cancelBtn.setOnClickListener {
                                findNavController().navigateUp()
                                myDialog.dismiss()

                            }

                            myDialog.show()
                        }
                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {
                        // show loading bar

                    }
                }
            }
        }
    }
}