
package com.example.projectfoodmanager.ui.auth.registerFragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
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
import com.google.android.gms.common.SupportErrorDialogFragment.newInstance
import com.google.android.material.datepicker.MaterialCalendar.newInstance
import dagger.hilt.android.AndroidEntryPoint
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


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

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        Locale.setDefault(Locale("pt"));
        binding.dateEt.setOnClickListener {
            initDatePicker(year,month,day)
        }

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

        var isValid = true
        //first_name
        if (binding.firstNameEt.text.isNullOrEmpty()){
            isValid = false
            binding.firstNameTL.isErrorEnabled=true
            binding.firstNameTL.error=getString(R.string.enter_first_name)
            //toast(getString(R.string.enter_first_name))
        }else{
            binding.firstNameTL.isErrorEnabled=false
        }

        //last_name
        if (binding.lastNameEt.text.isNullOrEmpty()){
            isValid = false
            binding.lastNameTL.isErrorEnabled=true
            binding.lastNameTL.error=getString(R.string.enter_last_name)
           // toast(getString(R.string.enter_last_name))
        }else{
            binding.lastNameTL.isErrorEnabled=false
        }

        //email
        if (binding.emailEt.text.isNullOrEmpty()){
            isValid = false
            binding.emailTL.isErrorEnabled=true
            binding.emailTL.error=getString(R.string.enter_email)
            //toast(getString(R.string.enter_email))
        }else if (!binding.emailEt.text.toString().isValidEmail()){
            isValid = false
            binding.emailTL.isErrorEnabled=true
            binding.emailTL.error=getString(R.string.invalid_email)
            //toast(getString(R.string.invalid_email))
        }else{
            binding.emailTL.isErrorEnabled=false
        }

        //aniversário
       if (binding.dateEt.text.isNullOrEmpty()){
           isValid = false
           binding.dateTL.isErrorEnabled=true
           binding.dateTL.error=getString(R.string.enter_birthdate)
            //toast(getString(R.string.enter_birthdate))
        }else{
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
            try {
                val dateTime: LocalDate = LocalDate.parse(binding.dateEt.text.toString(), formatter)
                if (dateTime >= LocalDate.now()){
                    //toast(getString(R.string.invalid_birthdate_2))
                    isValid = false
                    binding.dateTL.isErrorEnabled=true
                    binding.dateTL.error=getString(R.string.invalid_birthdate_2)
                }else{
                    binding.dateTL.isErrorEnabled=false
                }
            }
            catch (e: DateTimeException){
                isValid = false
                binding.dateTL.isErrorEnabled=true
                binding.dateTL.error=getString(R.string.invalid_birthdate)
            }

        }
        //genero
        if (binding.sexEt.text.isNullOrEmpty()){
            isValid = false
            binding.sexTL.isErrorEnabled=true
            binding.sexTL.error=getString(R.string.invalid_sex)
        }

        //password
        if(binding.passwordTL.isErrorEnabled)
            binding.passwordTL.isErrorEnabled=false

        if (binding.passEt.text.isNullOrEmpty()){
            isValid = false
            binding.passwordTL.isErrorEnabled=true
            binding.passwordTL.error=getString(R.string.enter_password)
            //toast(getString(R.string.enter_password))
        }else if (binding.passEt.text.toString().length < 8){
            isValid = false
            binding.passwordTL.isErrorEnabled=true
            binding.passwordTL.error=getString(R.string.invalid_password_1)

        }else if(binding.passEt.text.toString().compareTo(binding.passEtConf.text.toString()) != 0 ){
                isValid = false
                binding.passwordConfTL.isErrorEnabled=true
                binding.passwordConfTL.error=getString(R.string.invalid_password_2)
        }else{
            binding.passwordConfTL.isErrorEnabled=false
        }

        return isValid
    }

    private fun initDatePicker(year:Int,month:Int,day:Int) {

        // on below line we are creating a
        // variable for date picker dialog.
        val datePickerDialog = DatePickerDialog(
            // on below line we are passing context.
            requireContext(),
            { view, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our text view.

                //val editable = Editable.Factory.getInstance().newEditable(dayOfMonth.toString() + "/" + (monthOfYear + 1).toString() + "/" + year.toString())

                binding.dateEt.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1).toString() + "/" + year.toString())
            },
            // on below line we are passing year, month
            // and day for the selected date in our date picker.
            year,
            month,
            day
        )
        // at last we are calling show
        // to display our date picker dialog.
        datePickerDialog.show()
    }

}