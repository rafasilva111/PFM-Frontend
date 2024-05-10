package com.example.projectfoodmanager.presentation.profile.settings.sendUsAMessage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.miscellaneous.ApplicationReport
import com.example.projectfoodmanager.databinding.FragmentSendUsAMessageBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.MiscellaneousViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SendUsAMessageFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentSendUsAMessageBinding

    // viewModels
    private val miscellaneousViewModel by activityViewModels<MiscellaneousViewModel>()

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
        binding = FragmentSendUsAMessageBinding.inflate(layoutInflater)



        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUI()
        bindObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUI() {

        /**
         * General
         */

        Helper.changeMenuVisibility(false, requireActivity())
        Helper.changeTheme(false, activity, requireContext())


        binding.send.setOnClickListener {
            sendApplicationReport()

        }



        /**
         * Navigation
         */

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun sendApplicationReport() {
        if (validation())
            miscellaneousViewModel.postAppReport(ApplicationReport(title = binding.titleET.text.toString(), message = binding.descriptionET.text.toString()))

    }
    private fun validation(): Boolean {

        var isValid = true

        // title
        if (binding.titleET.text.isNullOrEmpty()){
            isValid = false
            binding.titleTL.isErrorEnabled=true
            binding.titleTL.error=getString(R.string.enter_title)
        }else{
            binding.titleTL.isErrorEnabled=false
        }

        // message
        if (binding.descriptionET.text.isNullOrEmpty()){
            isValid = false
            binding.descriptionTL.isErrorEnabled=true
            binding.descriptionTL.error=getString(R.string.enter_message)
        }else{
            binding.descriptionTL.isErrorEnabled=false
        }

        return isValid
    }

    private fun bindObservers() {
        miscellaneousViewModel.postAppReportLiveData.observe(viewLifecycleOwner) { result ->
            result.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast(getString(R.string.fragment_send_us_a_message_success))
                        findNavController().navigateUp()
                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        // todo falta aqui um loading bar
                    }
                }
            }
        }
    }


}