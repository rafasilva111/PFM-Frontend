
package com.example.projectfoodmanager.ui.auth.registerFragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.FireStorage.user_profile_images
import com.example.projectfoodmanager.util.actionResultCodes.GALLERY_REQUEST_CODE
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    val authViewModel: AuthViewModel by viewModels()


    private var file_uri: Uri? = null
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

        bindObservers()

        Locale.setDefault(Locale("pt"));

        binding.skipBiodata.setOnClickListener {
            // todo melhorar a estétitica
            if (validation()) {
                if (file_uri != null){
                    val fileName = UUID.randomUUID().toString() +".jpg"

                    val refStorage = Firebase.storage.reference.child("$user_profile_images$fileName")
                    refStorage.putFile(file_uri!!)
                        .addOnSuccessListener(
                            OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                                Log.d(TAG, "uploadImageToFirebase: success")
                                authViewModel.registerUser(getUserRequest())
                            })

                        ?.addOnFailureListener(OnFailureListener { e ->
                            Log.d(TAG, "uploadImageToFirebase: "+e)
                        })
                }
                else
                    authViewModel.registerUser(getUserRequest())

            }
        }

        binding.imageView.setOnClickListener {
            selectImageFromGallery()
        }
        binding.dateEt.setOnClickListener {
            initDatePicker(year,month,day)
        }

        binding.backIB.setOnClickListener {
                findNavController().navigateUp()
        }
        binding.registerBtn.setOnClickListener {
            if (validation()){
                findNavController().navigate(R.id.action_registerFragment_to_biodataFragment_navigation,Bundle().apply {
                    putParcelable("user",getUserRequest())
                    if (file_uri != null){
                        putString("uri",file_uri.toString())
                    }

                })
            }
        }
    }

    fun getUserRequest(): UserRequest {

        var sex = binding.sexEt.text.toString()
        if (sex == "Masculino")
            sex = "M"
        else if(sex == "Feminino")
            sex = "F"
        else if  (sex == "Nao responder")
                sex = "Nao responder"


        return UserRequest(
            first_name = binding.firstNameEt.text.toString(),
            last_name = binding.lastNameEt.text.toString(),
            email = binding.emailEt.text.toString(),
            birth_date = binding.dateEt.text.toString(),
            password = binding.passEt.text.toString(),
            sex = sex
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
            { _, year, monthOfYear, dayOfMonth ->
                // on below line we are setting
                // date to our text view.

                //val editable = Editable.Factory.getInstance().newEditable(dayOfMonth.toString() + "/" + (monthOfYear + 1).toString() + "/" + year.toString())
                var dayOfMonth_: String
                if (dayOfMonth<10)
                    dayOfMonth_ = "0$dayOfMonth"
                else
                    dayOfMonth_ = "$dayOfMonth"
                var monthOfYear_: String
                if ((monthOfYear + 1)<10)
                    monthOfYear_ = "0${monthOfYear + 1}"
                else
                    monthOfYear_ = "${monthOfYear + 1}"


                binding.dateEt.setText("$dayOfMonth_/$monthOfYear_/${year}")
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

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Please select..."
            ),
            GALLERY_REQUEST_CODE
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == GALLERY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {
            // Get the Uri of data

            file_uri = data.data
            if (file_uri != null) {
                binding.imageView.setImageURI(file_uri)
            }
        }
    }

    private fun bindObservers() {
        authViewModel.userRegisterLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
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


    override fun onResume() {
        requireActivity().window.decorView.systemUiVisibility = 0
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.main_color)
        super.onResume()
    }

    override fun onPause() {
        requireActivity().window.decorView.systemUiVisibility = 8192
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.background_1)

        super.onPause()
    }
}