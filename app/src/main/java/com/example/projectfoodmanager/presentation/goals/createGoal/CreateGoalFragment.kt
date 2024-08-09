package com.example.projectfoodmanager.presentation.goals.createGoal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.user.goal.GoalDTO
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.data.model.user.goal.fitnessReport.GenericReport
import com.example.projectfoodmanager.databinding.FragmentCreateGoalBinding
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
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
    }

    private fun setUI() {

        /**
         *  General
         * */

        binding.fragmentRegisterViewPager.adapter = GoalTabAdapter(requireActivity().supportFragmentManager, lifecycle,binding)
        binding.fragmentRegisterViewPager.isUserInputEnabled =false

        /**
         *  Navigation
         * */

        binding.backIB.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle(getString(R.string.fragment_register_cancel))
                .setMessage(resources.getString(R.string.fragment_register_cancel_description))
                .setPositiveButton(getString(R.string.COMMON_DIALOG_YES)) { _, _ ->
                    // Adicione aqui o código para apagar o registro
                    findNavController().navigateUp()
                }
                .setNegativeButton(getString(R.string.COMMON_DIALOG_NO)) { dialog, _ ->
                    // Adicione aqui o código para cancelar a exclusão do registro
                    dialog.dismiss()
                }
                .show()
        }

        /** Goal  */




        /**
         *  Tab Layout
         * */


        binding.fragmentRegisterTabLayout.removeAllTabs()

        binding.fragmentRegisterTabLayout.addTab(binding.fragmentRegisterTabLayout.newTab().setText("CALORIES"))
        binding.fragmentRegisterTabLayout.addTab(binding.fragmentRegisterTabLayout.newTab().setText("CARBOHYDRATES"))
        binding.fragmentRegisterTabLayout.addTab(binding.fragmentRegisterTabLayout.newTab().setText("FAT"))
        binding.fragmentRegisterTabLayout.addTab(binding.fragmentRegisterTabLayout.newTab().setText("OVERVIEW"))


        // enables back from comments
        binding.fragmentRegisterViewPager.isSaveEnabled = false

        binding.fragmentRegisterTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null)
                    binding.fragmentRegisterViewPager.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }


        })

        binding.fragmentRegisterViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val selectedTab: TabLayout.Tab? = binding.fragmentRegisterTabLayout.getTabAt(position)
                when(selectedTab!!.text) {
                    "CALORIES" -> {
                        binding.descStep1TV.visibility = View.VISIBLE
                        binding.descStep2TV.visibility = View.GONE
                    }
                    "CARBOHYDRATES" -> {
                        binding.descStep1TV.visibility = View.GONE
                        binding.descStep2TV.visibility = View.VISIBLE
                        binding.descStep3TV.visibility = View.GONE
                    }
                    "FAT" -> {
                        binding.descStep2TV.visibility = View.GONE
                        binding.descStep3TV.visibility = View.VISIBLE

                        binding.stepsTab.visibility = View.VISIBLE
                        binding.fragmentGoalStepsTabOverview.visibility = View.GONE
                    }
                    "OVERVIEW" -> {
                        binding.stepsTab.visibility = View.GONE
                        binding.fragmentGoalStepsTabOverview.visibility = View.VISIBLE
                    }
                }

                binding.fragmentRegisterTabLayout.selectTab(selectedTab)
            }
        })



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
        changeMenuVisibility(false, activity)
        changeTheme(false, activity, requireContext())

    }

    companion object{
        lateinit var fitnessReport: FitnessReport
        lateinit var goalGenericReport: GenericReport
        var userGoal: GoalDTO = GoalDTO()
    }
}