package com.example.projectfoodmanager.presentation.goals

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.databinding.FragmentUpdateBiodataBinding
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpdateBiodata : Fragment() {
    lateinit var binding: FragmentUpdateBiodataBinding
    val userViewModel: UserViewModel by viewModels()
    val TAG: String = "ProfileFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {
        changeVisibilityMenu(state = false)
        binding = FragmentUpdateBiodataBinding.inflate(layoutInflater)

        bindObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.backIB.setOnClickListener {
            val dialogBinding : View = layoutInflater.inflate(R.layout.dialog_confirmation_generic, null);

            val myDialog = Dialog(requireContext())
            myDialog.setContentView(dialogBinding)

            // create alert dialog
            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogBinding.findViewById<TextView>(R.id.tvDescription).text = "Are you sure?"
            dialogBinding.findViewById<TextView>(R.id.tvTitle).text = "Atenção"

            dialogBinding.findViewById<Button>(R.id.btn_conf_Yes).setOnClickListener {
                findNavController().navigateUp()
                myDialog.dismiss()
            }

            dialogBinding.findViewById<Button>(R.id.btn_conf_cancel).setOnClickListener {
                myDialog.dismiss()

            }

            myDialog.show()
        }

        if (isOnline(requireContext())){
            binding.registerBtn.setOnClickListener {
                if(validation()){
                    userViewModel.updateUser(getUserRequest())
                }
            }
        }
        else{
            //está offline
            val popUpShow = PopUpFragment()
            popUpShow.show((activity as AppCompatActivity).supportFragmentManager,"showUpFrgament")
        }
        super.onViewCreated(view, savedInstanceState)
    }



    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    private fun changeVisibilityMenu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

    fun getUserRequest(): UserDTO {
        var activityLevel = 0.0F
        when(binding.activityLevelRg.checkedRadioButtonId){
            R.id.op1_RB-> activityLevel= 1.2F
            R.id.op2_RB-> activityLevel= 1.375F
            R.id.op3_RB-> activityLevel= 1.465F
            R.id.op4_RB-> activityLevel= 1.55F
            R.id.op5_RB-> activityLevel= 1.725F
            R.id.op6_RB-> activityLevel= 1.9F
        }
        return UserDTO(
            height = binding.heightEt.text.toString().toFloat(),
            weight = binding.weightEt.text.toString().toFloat(),
            activity_level = activityLevel,
            )
    }

    fun validation(): Boolean {
        var isValid = true
        val heightTxt = binding.heightEt.text.toString()

        if (heightTxt.isNullOrEmpty()) {
            isValid = false
            binding.heightTL.isErrorEnabled=true
            binding.heightTL.error=getString(R.string.height_problem)
            //toast(getString(R.string.heightEt_problem))
        } else if (heightTxt.toIntOrNull() != null) {
            if (heightTxt.toIntOrNull()  !in 120..300){
                isValid = false
                binding.heightTL.isErrorEnabled=true
                binding.heightTL.error=getString(R.string.height_problem)
                //toast(getString(R.string.heightEt_problem))
            }
        } else if (heightTxt.toFloatOrNull() != null){
            val heightFloat = heightTxt.toFloatOrNull()
            if (heightFloat!! !in 1.20..3.0) {
                isValid = false
                binding.heightTL.isErrorEnabled=true
                binding.heightTL.error=getString(R.string.height_problem)
                //toast(getString(R.string.heightEt_problem))
            } else {
                binding.heightEt.setText((heightFloat * 100).toString())
                if ((heightFloat * 100) !in 120.0..300.0){
                    isValid = false
                    binding.heightTL.isErrorEnabled=true
                    binding.heightTL.error=getString(R.string.height_problem)
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
            binding.weightTL.error=getString(R.string.weight_problem_2)
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

    private fun bindObservers() {

        userViewModel.userUpdateResponseLiveData.observe(viewLifecycleOwner, Observer { userSessionResponse ->
            userSessionResponse.getContentIfNotHandled()?.let{

                when (it) {
                    is NetworkResult.Success -> {
                        toast("Dados atualizados com sucesso")
                        findNavController().navigateUp()

                    }
                    is NetworkResult.Error -> {
                        toast("Dados não atualizados, alguma coisa se passou.")
                    }
                    is NetworkResult.Loading -> {
                        // show loading bar
                        //todo falta aqui uma loading bar

                    }
                }
            }
        })
    }

}