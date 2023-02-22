
package com.example.projectfoodmanager.ui.auth.registerFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.MainActivity
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.databinding.FragmentRegisterBiodataBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BioDataFragment : Fragment() {

    val TAG: String = "BioDataFragment"
    lateinit var binding: FragmentRegisterBiodataBinding
    val viewModel: AuthViewModel by viewModels()
    var objUser: User? = null
    val requiredFields: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBiodataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
            // TODO: alertar que antes de voltar atras
        }
        objUser = arguments?.getParcelable("user")
        if (objUser==null)
            Log.d(TAG, "Something went wrong whit user object")
        observer()
        binding.registerBtn.setOnClickListener {
            if (validation()) {
                val userRequest = getUserRequest()
                viewModel.registerUser(userRequest)
            }
        }
    }
    fun observer() {
        viewModel.successful.observe(viewLifecycleOwner) { successful ->
            if (successful == true){
                toast("Sucess")
                viewModel.navigateToPage()
                findNavController().navigate(R.id.action_registerFragment_to_home_navigation)
            }else if(successful == false){
                if (viewModel.error.value!!.contains("The email address is already"))
                    toast(getString(R.string.invalid_email_2))
                else
                    toast("Failuire")
                viewModel.navigateToPage()
            }
        }
    }


    fun getUserRequest():UserRequest {

        return UserRequest(
            first_name = objUser!!.first_name,
            last_name = objUser!!.last_name,
            email = objUser!!.email,
            birth_date = objUser!!.birth_date,
            password = objUser!!.password,
            sex = objUser!!.sex,
            height = binding.heightEt.text.toString(),
            weight = binding.weightEt.text.toString(),
            activity_level = binding.activityLevelRg.checkedRadioButtonId.toString(),

            )

       /* else{
            return UserRequest(
                first_name = objUser!!.first_name,
                last_name = objUser!!.last_name,
                email = objUser!!.email,
                birth_date = objUser!!.birth_date,
                password = objUser!!.password,
                sex = objUser!!.sex
                )
        }*/

    }

    fun validation(): Boolean {
        var isValid = true
        val heightTxt = binding.heightEt.text.toString()
        if (heightTxt.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.heightEt_problem))
        } else {
            val heightInt = heightTxt.toIntOrNull()
            if (heightInt != null) {
                if (heightInt !in 120..300){
                    isValid = false
                    toast(getString(R.string.heightEt_problem))
                }
            } else {
                val heighFloat = heightTxt.toFloatOrNull()
                if (heighFloat != null)
                    if (heighFloat !in 1.20..3.0) {
                        isValid = false
                        toast(getString(R.string.heightEt_problem))
                    } else {
                        binding.heightEt.setText((heighFloat * 100).toString())
                    }
            }


        }

        if (binding.weightEt.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_weight))
        }
        else{
            val weight = binding.weightEt.text.toString().toFloatOrNull()
            if (weight == null){
                isValid = false
                toast(getString(R.string.enter_weight))
            }
            else{
                if (weight !in 30.0..200.0) {
                    isValid = false
                    toast(getString(R.string.weightEt_problem_2))
                }
            }
        }

        if (binding.activityLevelRg.checkedRadioButtonId == null) {
            isValid = false
            toast(getString(R.string.enter_activity_level))
        }
        return isValid
    }

}