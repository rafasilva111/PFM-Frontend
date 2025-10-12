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
import com.example.projectfoodmanager.MainActivity
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.user.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentSettingsBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.network.NetworkConnectivity
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    // binding
    private lateinit var binding: FragmentSettingsBinding

    // viewModels
    private val userViewModel by activityViewModels<UserViewModel>()

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
        if (!this::binding.isInitialized) {
            binding = FragmentSettingsBinding.inflate(layoutInflater)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()

        super.onViewCreated(view, savedInstanceState)
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

        binding.SWNotify.isChecked = user.fmcToken == "-1"

        binding.SWPrivacy.isChecked = user.profileType== "PRIVATE"

        // todo
        binding.SWCarinhosMode.isChecked = false

    }

    private fun setUI() {

        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        Helper.changeTheme(false, activity, requireContext())
        binding.header.titleTV.text = "Definições"

        /** internet connection observer*/

        val networkConnectivityObserver = NetworkConnectivity(requireContext())

        user = sharedPreference.getUserSession()

        /**
         * Toggle buttons
         */

        setToggleButtonsInitialState()

        setToggleButtonsFunctions()


        /**
         * Buttons
         */

        binding.header.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.securityCV.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_securityFragment)
        }

        binding.languagesCV.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_languagesFragment)
        }

        binding.aboutUsCV.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_aboutUsFragment)
        }

        binding.faqsCV.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_FAQsFragment)
        }

        binding.sendUsAMessageCV.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_sendUsAMessageFragment)
        }

        binding.BTNDeleteAccountB.setOnClickListener {
            if (MainActivity.internetConnection){
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_logout)
                    .setTitle(getString(R.string.settings_fragment_dialog_title))
                    .setMessage(getString(R.string.settings_fragment_dialog_desc))
                    .setPositiveButton(getString(R.string.COMMON_DIALOG_YES)) { _, _ ->
                        // Adicione aqui o código para apagar o registro
                        userViewModel.deleteUserAccount()
                        changeMenuVisibility(false, activity)
                        findNavController().navigate(R.id.action_settingsFragment_to_login)

                    }
                    .setNegativeButton(getString(R.string.COMMON_DIALOG_NO)) { dialog, _ ->
                        // Adicione aqui o código para cancelar a exclusão do registro
                        dialog.dismiss()
                    }
                    .show()

            }
            else{
                toast("You don't have internet connection...",type = ToastType.ALERT)
            }

        }

    }


    override fun onStop() {
        super.onStop()

        // update user
        if ( fmcToken != null ||profileType != null)
            userViewModel.updateUser(UserRequest(fmc_token = fmcToken,profileType = profileType))
    }
}