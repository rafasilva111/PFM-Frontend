package com.example.projectfoodmanager.presentation.goals.createGoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.user.goal.GoalDTO
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.databinding.FragmentCreateGoalBinding
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.SharedPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateGoalFragment : Fragment() {


    /** binding */
    private lateinit var binding: FragmentCreateGoalBinding

    /** viewModels */

    /** variables */
    private val TAG: String = "BlankFragment"


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
        bindObservers()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUI() {

        /**
         *  General
         * */

        /** Refresh Goal */
        userGoal =GoalDTO()

        /**
         *  Navigation
         * */

        binding.backIB.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle(getString(R.string.fragment_register_cancel))
                .setMessage(resources.getString(R.string.fragment_register_cancel_description))
                .setPositiveButton(getString(R.string.dialog_yes)) { _, _ ->
                    // Adicione aqui o código para apagar o registro
                    findNavController().navigateUp()
                }
                .setNegativeButton(getString(R.string.dialog_no)) { dialog, _ ->
                    // Adicione aqui o código para cancelar a exclusão do registro
                    dialog.dismiss()
                }
                .show()
        }

        /** Goal  */

        binding.fragmentRegisterViewPager.adapter = GoalTabAdapter(requireActivity().supportFragmentManager, lifecycle,binding)
        binding.fragmentRegisterViewPager.isUserInputEnabled =false



    }

    private fun bindObservers() {

    }

    override fun onStart() {

        loadUI()
        super.onStart()
    }

    private fun loadUI() {


        /**
         *  General
         * */

        val activity = requireActivity()
        Helper.changeMenuVisibility(false, activity)
        Helper.changeStatusBarColor(false, activity, requireContext())

    }

    companion object{
        lateinit var fitnessReport: FitnessReport
        var userGoal: GoalDTO = GoalDTO()
    }
}