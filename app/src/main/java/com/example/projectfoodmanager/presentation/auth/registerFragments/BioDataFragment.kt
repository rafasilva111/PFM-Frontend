
package com.example.projectfoodmanager.ui.auth.registerFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import androidx.core.view.size
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
    private var activityLevel:String=""


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
        //observer()

        binding.activityLevelRg.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.op1_RB-> activityLevel="sedentario"
                R.id.op2_RB-> activityLevel="leve"
                R.id.op3_RB-> activityLevel="moderado"
                R.id.op4_RB-> activityLevel="ativo"
                R.id.op5_RB-> activityLevel="muito_ativo"
                R.id.op6_RB-> activityLevel="extra_ativo"
            }

        }


        binding.registerBtn.setOnClickListener {
            if (validation()) {
                val userRequest = getUserRequest()
                viewModel.registerUser(userRequest)
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
            activity_level = activityLevel,

            )
    }

    fun validation(): Boolean {
        var isValid = true
        val heightTxt = binding.heightEt.text.toString()

        if (heightTxt.isNullOrEmpty()) {
            isValid = false
            binding.heightTL.isErrorEnabled=true
            binding.heightTL.error=getString(R.string.heightEt_problem)
            //toast(getString(R.string.heightEt_problem))
        } else if (heightTxt.toIntOrNull() != null) {
            if (heightTxt.toIntOrNull()  !in 120..300){
                isValid = false
                binding.heightTL.isErrorEnabled=true
                binding.heightTL.error=getString(R.string.heightEt_problem)
                //toast(getString(R.string.heightEt_problem))
            }
        } else if (heightTxt.toFloatOrNull() != null){
            val heighFloat = heightTxt.toFloatOrNull()
            if (heighFloat!! !in 1.20..3.0) {
                isValid = false
                binding.heightTL.isErrorEnabled=true
                binding.heightTL.error=getString(R.string.heightEt_problem)
                //toast(getString(R.string.heightEt_problem))
            } else {
                binding.heightEt.setText((heighFloat * 100).toString())
            }
        }else{
            binding.heightTL.isErrorEnabled=false
        }

        if (binding.weightEt.text.toString().isNullOrEmpty()) {
            isValid = false
            binding.weightTL.isErrorEnabled=true
            binding.weightTL.error=getString(R.string.enter_weight)
            //toast(getString(R.string.enter_weight))
        }else if (binding.weightEt.text.toString().toFloatOrNull() == null){
            isValid = false
            binding.weightTL.isErrorEnabled=true
            binding.weightTL.error=getString(R.string.enter_weight)
            //toast(getString(R.string.enter_weight))
        }else if (binding.weightEt.text.toString().toFloatOrNull()!! !in 30.0..200.0) {
            isValid = false
            binding.weightTL.isErrorEnabled=true
            binding.weightTL.error=getString(R.string.weightEt_problem_2)
            //toast(getString(R.string.weightEt_problem_2))
        }else{
            binding.weightTL.isErrorEnabled=false
        }

        if (binding.activityLevelRg.checkedRadioButtonId == -1) {
            isValid = false
            //binding.activityLevelRg.

            binding.op1RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op1RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op2RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op2RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op3RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op3RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op4RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op4RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op5RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op5RB.setTextColor(resources.getColor(R.color.red,null))
            binding.op6RB.buttonTintList=context?.resources?.getColorStateList(R.color.red,null)
            binding.op6RB.setTextColor(resources.getColor(R.color.red,null))

            binding.errorActivityLevelTV.visibility=View.VISIBLE
            binding.errorActivityLevelTV.text=getString(R.string.enter_activity_level)
            //toast(getString(R.string.enter_activity_level))
        }else{
            binding.op1RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op1RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op2RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op2RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op3RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op3RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op4RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op4RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op5RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op5RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op6RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey_2,null)
            binding.op6RB.setTextColor(resources.getColor(R.color.black,null))
            binding.errorActivityLevelTV.visibility=View.INVISIBLE
        }
        return isValid
    }

}