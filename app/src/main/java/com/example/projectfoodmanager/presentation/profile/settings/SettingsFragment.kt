package com.example.projectfoodmanager.presentation.profile.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentSettingsBinding
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.ProfileType
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentSettingsBinding

    // viewModels
    private val authViewModel by activityViewModels<AuthViewModel>()

    // constants
    private lateinit var user: User
    private var profileType: String? = null
    private var fmcToken: String? = null

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = sharedPreference.getUserSession()

        /**
         * Toggle buttons
         */

        setToggleButtonsInitialState()

        setToggleButtonsFunctions()


        binding.CLSecurity.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_securityFragment)
        }
        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.CL6.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_blankFragment)
        }

    }

    private fun setToggleButtonsFunctions() {

        binding.SWDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch to dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Switch to light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // todo pensar melhor nisto
        binding.SWOfflineMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

            }
            else{

            }
        }

        binding.SWNotify.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                fmcToken = "-1"
            }
            else{
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    fmcToken = token
                }
            }
        }

        binding.SWPrivacy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                profileType = ProfileType.PRIVATE
            }
            else{
                profileType = ProfileType.PUBLIC
            }
        }

        binding.SWCarinhosMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                    // Light mode is active, switch to dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    // Dark mode is active, switch to light mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                recreate(requireActivity())
            }

        }

    }

    private fun setToggleButtonsInitialState() {

        binding.SWDarkMode.isChecked = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        binding.SWOfflineMode.isChecked = !isOnline(requireContext())

        binding.SWNotify.isChecked = user.fmc_token == "-1"

        binding.SWPrivacy.isChecked = user.profile_type== "PRIVATE"

        // todo
        binding.SWCarinhosMode.isChecked = false

    }

    private fun changeVisib_Menu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

    override fun onResume() {
        requireActivity().window.decorView.systemUiVisibility = 0
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.main_color)
        changeVisib_Menu(false)

        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().window.decorView.systemUiVisibility = 8192
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.background_1)
        changeVisib_Menu(true)

        // update user

        authViewModel.updateUser(UserRequest(fmc_token = fmcToken,profile_type = profileType))
    }
}