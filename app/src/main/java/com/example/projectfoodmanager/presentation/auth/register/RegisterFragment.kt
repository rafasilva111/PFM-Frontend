
package com.example.projectfoodmanager.presentation.auth.register

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.data.model.util.ValidationError
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.util.Helper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    /** binding */
    lateinit var binding: FragmentRegisterBinding

    /** viewModels */

    /** constants */
    private val TAG: String = "RegisterFragment"


    /** injects */


    /** adapters */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentRegisterBinding.inflate(layoutInflater)

        }

        binding.fragmentRegisterViewPager.adapter = RegisterTabAdapter(requireActivity().supportFragmentManager, lifecycle,binding)
        binding.fragmentRegisterViewPager.isUserInputEnabled =false

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

        Helper.changeStatusBarColor(false, requireActivity(), requireContext())

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
        /**
         *  Tab Layout
         * */

        binding.fragmentRegisterTabLayout.removeAllTabs()

        binding.fragmentRegisterTabLayout.addTab(binding.fragmentRegisterTabLayout.newTab().setText("ACCOUNT"))
        binding.fragmentRegisterTabLayout.addTab(binding.fragmentRegisterTabLayout.newTab().setText("ACCOUNT_DETAILS"))
        binding.fragmentRegisterTabLayout.addTab(binding.fragmentRegisterTabLayout.newTab().setText("ACCOUNT_BIODATA"))


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
                    "ACCOUNT" -> {
                        binding.descStep1TV.visibility = View.VISIBLE
                        binding.descStep2TV.visibility = View.GONE
                    }"ACCOUNT_DETAILS" -> {
                        binding.descStep1TV.visibility = View.GONE
                        binding.descStep2TV.visibility = View.VISIBLE
                        binding.descStep3TV.visibility = View.GONE
                    }"ACCOUNT_BIODATA" -> {
                        binding.descStep2TV.visibility = View.GONE
                        binding.descStep3TV.visibility = View.VISIBLE
                    }
                }

                binding.fragmentRegisterTabLayout.selectTab(selectedTab)
            }
        })

    }

    private fun bindObservers() {

    }



    companion object{
        var user: UserDTO = UserDTO()

        /** Image */
        var imgURI: Uri? = null
        var selectedAvatar: String? = null

        var errors: ValidationError? = null

    }
}