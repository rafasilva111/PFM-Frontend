
package com.example.projectfoodmanager.ui.auth.registerFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.util.*
import dagger.hilt.android.AndroidEntryPoint
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    val TAG: String = "RegisterFragment"
    lateinit var binding: FragmentRegisterBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        val genders = resources.getStringArray(R.array.gender_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_register_gender,genders)
        binding.sexEt.setAdapter(arrayAdapter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backIB.setOnClickListener {
                findNavController().navigateUp()
        }
        binding.registerBtn.setOnClickListener {
            if (validation()){
                val user = getUserObj()
                findNavController().navigate(R.id.action_registerFragment_to_biodataFragment_navigation,Bundle().apply {
                    putParcelable("user",user)
                })
            }
        }
    }

    fun getUserObj(): User {
        return User(
            first_name = binding.firstNameEt.text.toString(),
            last_name = binding.lastNameEt.text.toString(),
            email = binding.emailEt.text.toString(),
            birth_date = binding.dateEt.text.toString(),
            password = binding.passEt.text.toString(),
            sex = binding.sexEt.text.toString()
        )
    }

    fun validation(): Boolean {

        //first_name
        if (binding.firstNameEt.text.isNullOrEmpty()){
            toast(getString(R.string.enter_first_name))
            return false
        }
        //last_name
        if (binding.lastNameEt.text.isNullOrEmpty()){
            toast(getString(R.string.enter_last_name))
            return false
        }
        //email
        if (binding.emailEt.text.isNullOrEmpty()){
            toast(getString(R.string.enter_email))
            return false
        }else{
            if (!binding.emailEt.text.toString().isValidEmail()){
                toast(getString(R.string.invalid_email))
                return false
            }
        }
        //aniversário
        if (binding.dateEt.text.isNullOrEmpty()){
            toast(getString(R.string.enter_birthdate))
            return false
        }
        else{
            val date = binding.dateEt.text.toString().split("/")
            if (date.size !=3){
                toast(getString(R.string.invalid_birthdate))
                return false
            }
            var day = date[0]
            var month = date[1]
            var year = date[2]
            if (day.length==1){
                day = "0"+day
            }

            if (month.length==1){
                month = "0"+month
            }
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            try {
                val dateTime: LocalDate = LocalDate.parse(day+"/"+month+"/"+year, formatter)
                if (dateTime >= LocalDate.now()){
                    toast(getString(R.string.invalid_birthdate_2))
                    return false
                }
                binding.dateEt.setText(day+"/"+month+"/"+year)
            }
            catch (e: DateTimeException){
                toast(getString(R.string.invalid_birthdate))
                return false
            }

            
        }
        //genero
        if (binding.dateEt.text.isNullOrEmpty()){

        }
        //password
        if (binding.passEt.text.isNullOrEmpty()){
            toast(getString(R.string.enter_password))
            return false
        }else{
            if (binding.passEt.text.toString().length < 8){

                toast(getString(R.string.invalid_password))
                return false
            }
            else{
                if(binding.passEt.text.toString().compareTo(binding.passEtConf.text.toString()) != 0 ){
                    toast(getString(R.string.invalid_password_2))
                    return false
                }
            }
        }
        return true
    }

}