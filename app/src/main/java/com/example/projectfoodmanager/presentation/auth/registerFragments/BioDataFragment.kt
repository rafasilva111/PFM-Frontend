
package com.example.projectfoodmanager.ui.auth.registerFragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.databinding.FragmentRegisterBiodataBinding
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.util.TokenManager
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class BioDataFragment : Fragment() {


    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    val TAG: String = "BioDataFragment"
    lateinit var binding: FragmentRegisterBiodataBinding
    val authViewModel: AuthViewModel by viewModels()
    var objUser: UserRequest? = null
    private var fileUri: String? = null
    private var activityLevel : Float = 0.0f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBiodataBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
            // TODO: alertar que antes de voltar atras
        }
        objUser = arguments?.getParcelable("user")
        fileUri = arguments?.getString("uri")
        if (objUser==null)
            Log.d(TAG, "Something went wrong whit user object")


        binding.activityLevelRg.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId){
                R.id.op1_RB-> activityLevel= 1.2F
                R.id.op2_RB-> activityLevel= 1.375F
                R.id.op3_RB-> activityLevel= 1.465F
                R.id.op4_RB-> activityLevel= 1.55F
                R.id.op5_RB-> activityLevel= 1.725F
                R.id.op6_RB-> activityLevel= 1.9F
            }

        }


        binding.registerBtn.setOnClickListener {
            if (validation()) {
                if (fileUri != null){
                    val path = "${FireStorage.user_profile_images}${UUID.randomUUID().toString() +".jpg"}"

                    val refStorage = Firebase.storage.reference.child("$path")
                    refStorage.putFile(Uri.parse(fileUri!!))
                        .addOnSuccessListener {
                            Log.d(TAG, "uploadImageToFirebase: success")
                            var user = getUserRequest()
                            user.img_source = path
                            authViewModel.registerUser(user)
                        }
                        .addOnFailureListener(OnFailureListener { e ->
                            Log.d(TAG, "uploadImageToFirebase: "+e)
                        })
                }
                else
                    authViewModel.registerUser(getUserRequest())
            }
        }
    }


    private fun bindObservers() {
        authViewModel.userRegisterLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        findNavController().navigate(R.id.action_registerFragment_to_home_navigation)
                        toast(getString(R.string.user_registered_successfully))
                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        // todo falta aqui um loading bar
                    }
                }
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
            height = binding.heightEt.text.toString().toFloat(),
            weight = binding.heightEt.text.toString().toFloat(),
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
            val heightFloat = heightTxt.toFloatOrNull()
            if (heightFloat!! !in 1.20..3.0) {
                isValid = false
                binding.heightTL.isErrorEnabled=true
                binding.heightTL.error=getString(R.string.heightEt_problem)
                //toast(getString(R.string.heightEt_problem))
            } else {
                binding.heightEt.setText((heightFloat * 100).toString())
                if ((heightFloat * 100) !in 120.0..300.0){
                    isValid = false
                    binding.heightTL.isErrorEnabled=true
                    binding.heightTL.error=getString(R.string.heightEt_problem)
                    //toast(getString(R.string.heightEt_problem))
                }
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