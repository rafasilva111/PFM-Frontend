
package com.example.projectfoodmanager.ui.auth.registerFragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.BuildConfig
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
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.lang.ref.WeakReference
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var fileName: String? = null
    lateinit var finalUri: Uri
    val authViewModel: AuthViewModel by viewModels()
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


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

        activityResultLauncher  =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode== AppCompatActivity.RESULT_OK) {
                    var extras: Bundle? = result.data?.extras
                    var imageUri: Uri
                    var imageBitmap = extras?.get("data") as Bitmap
                    var imageResult: WeakReference<Bitmap> = WeakReference(
                        Bitmap.createScaledBitmap(
                            imageBitmap, imageBitmap.width, imageBitmap.height, false
                        ).copy(
                            Bitmap.Config.RGB_565, true
                        )
                    )
                    var bm = imageResult.get()

                    // todo look into this
                    imageUri = saveImage(bm, requireContext())
                    launchImageCrop(imageUri)
                }
                else{
                    Log.d(TAG, "onCreateView: Something went wrong on registerForActivityResult")
                }
            }

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

            //USER CONFIRMATION DIALOG
            // set the custom layout
            val dialogBinding : View = layoutInflater.inflate(R.layout.dialog_confirmation_camera_gallery, null);

            val myDialog = Dialog(requireContext())
            myDialog.setContentView(dialogBinding)

            // create alert dialog
            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cameraBtn = dialogBinding.findViewById<Button>(R.id.btnConfCamera)
            val galleryBtn = dialogBinding.findViewById<Button>(R.id.btnConfGallery)
            val cancelBtn = dialogBinding.findViewById<Button>(R.id.btnConfCancel)

            cameraBtn.setOnClickListener {
                if (checkPermission()) {
                    pickFromCamera()
                }

                else{
                    toast( "Allow all permissions")
                    requestPermission()
                }

                myDialog.dismiss()
            }

            galleryBtn.setOnClickListener {
                if (checkPermission()) {
                    pickFromGallery()
                }

                else{
                    toast( "Allow all permissions")
                    requestPermission()
                }
                myDialog.dismiss()
            }

            cancelBtn.setOnClickListener {
                myDialog.dismiss()
            }

            myDialog.show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
            }
        }

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri :Uri ?= UCrop.getOutput(data!!)

            setImage(resultUri!!)

            if (fileName == null)
                fileName = UUID.randomUUID().toString() + ".png"

            authViewModel.updateUser(UserRequest(img_source = fileName))
            val storageRef = Firebase.storage.reference.child("$user_profile_images$fileName")

            storageRef.putFile(resultUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Image upload success
                    // You can perform additional operations here if needed

                    // Get the download URL of the uploaded image
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageURL = downloadUri.toString()
                        // Do something with the imageURL (e.g., save it in your userSession)

                        // Update the ImageView with the uploaded image
                        Glide.with(binding.imageView.context)
                            .load(imageURL)
                            .into(binding.imageView)
                    }
                }
                .addOnFailureListener { exception ->
                    // Image upload failed
                    // Handle the failure gracefully
                }
            finalUri=resultUri

            // todo
            saveEditedImage()
        }
    }

    private fun saveEditedImage() {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, finalUri)
        saveMediaToStorage(bitmap)
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

    private fun saveImage(image: Bitmap?, context: Context): Uri {
        val imageFolder= File(context.cacheDir,"images")
        var uri: Uri? = null

        try {

            imageFolder.mkdirs()
            val file = File(imageFolder,"captured_image.png")
            val stream = FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
            uri= FileProvider.getUriForFile(Objects.requireNonNull(requireActivity().applicationContext),
                BuildConfig.APPLICATION_ID + ".provider", file);


        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return uri!!

    }

    private fun launchImageCrop(uri: Uri) {


        var destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        var options: UCrop.Options= UCrop.Options()
        options.setCropGridColor(Color.TRANSPARENT)
        options.setStatusBarColor(resources.getColor(R.color.main_color))

        startActivityForResult(
            UCrop.of(Uri.parse(uri.toString()), Uri.fromFile(File(requireContext().cacheDir,destination)))
            .withOptions(options)
            .withAspectRatio(3F, 4F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(2000, 2000).getIntent(requireContext()), UCrop.REQUEST_CROP);
    }

    private fun pickFromGallery() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    private fun pickFromCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activityResultLauncher.launch(intent)
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            100
        )
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireActivity().contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            toast("Saved to Photos")
        }
    }

    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(binding.imageView)
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