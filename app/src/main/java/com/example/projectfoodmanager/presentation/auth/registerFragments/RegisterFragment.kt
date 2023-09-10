
package com.example.projectfoodmanager.ui.auth.registerFragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.AvatarGVAdapter
import com.example.projectfoodmanager.BuildConfig
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.Avatar.Companion.avatarArrayList
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.FireStorage.user_profile_images
import com.example.projectfoodmanager.util.actionResultCodes.GALLERY_REQUEST_CODE
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.time.DateTimeException
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var fileName: String? = null
    lateinit var finalUri: Uri
    private var selectedAvatar: String? = null
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
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,genders)

        binding.sexEt.setAdapter(arrayAdapter)

        activityResultLauncher  =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode== AppCompatActivity.RESULT_OK) {
                    val extras: Bundle? = result.data?.extras
                    val imageUri: Uri
                    val imageBitmap = extras?.get("data") as Bitmap
                    val imageResult: WeakReference<Bitmap> = WeakReference(
                        Bitmap.createScaledBitmap(
                            imageBitmap, imageBitmap.width, imageBitmap.height, false
                        ).copy(
                            Bitmap.Config.RGB_565, true
                        )
                    )
                    val bm = imageResult.get()

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

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindObservers()

        Locale.setDefault(Locale("pt"));

        binding.skipBiodata.setOnClickListener {
            // todo melhorar a estétitica
            if (validation()) {
                if (file_uri != null){
                    val fileName = UUID.randomUUID().toString() +".jpg"

                    val refStorage = Firebase.storage.reference.child("$user_profile_images$fileName")
                    refStorage.putFile(file_uri!!)
                        .addOnSuccessListener {
                            Log.d(TAG, "uploadImageToFirebase: success")
                            authViewModel.registerUser(getUserRequest())
                        }

                        .addOnFailureListener { e ->
                            Log.d(TAG, "uploadImageToFirebase: $e")
                        }
                }
                else

                    authViewModel.registerUser(getUserRequest())

            }else{
                Toast(context).showCustomToast ("Por favor preencha os campos em falta", requireActivity(),ToastType.ERROR)
            }
        }

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.uploadImageFB.setOnClickListener {

            //USER CONFIRMATION DIALOG
            // set the custom layout
            val dialogBinding : View = layoutInflater.inflate(R.layout.dialog_confirmation_camera_gallery, null);

            val myDialog = Dialog(requireContext())
            myDialog.setContentView(dialogBinding)

            // create alert dialog
            myDialog.setCancelable(true)
            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val cameraIV = dialogBinding.findViewById<ImageView>(R.id.cameraIV)
            val galleryIV = dialogBinding.findViewById<ImageView>(R.id.galletyIV)
            val cancelBtn = dialogBinding.findViewById<Button>(R.id.btnConfCancel)

            cameraIV.setOnClickListener{
                if (checkPermission()) {
                    pickFromCamera()
                }else{
                    toast( "Allow all permissions")
                    requestPermission()
                }

                myDialog.dismiss()
            }

            galleryIV.setOnClickListener {
                if (checkPermission()) {
                    pickFromGallery()
                }else{
                    toast( "Allow all permissions")
                    requestPermission()
                }
                myDialog.dismiss()
            }


            val avatarGV= dialogBinding.findViewById<GridView>(R.id.gvAvatar)

            val adapter = AvatarGVAdapter(requireContext(), avatarArrayList)
            avatarGV.adapter = adapter

            avatarGV.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->

                val avatar= adapter.getItem(position)

                if (avatar!!.reserved){
                    Toast(context).showCustomToast ("Este avatar apenas esta disponivel para VIP!\n Registe-se e depois pode adquirir o VIP", requireActivity(),ToastType.VIP)
                }else{
                    // Handle the item selection here
                    selectedAvatar = avatar.getName()

                    binding.imageView.setImageResource(avatar.imgId)

                    binding.imageView.tag=ImageTagsConstants.SELECTED_AVATAR
                    myDialog.dismiss()

                }
            }


            cancelBtn.setOnClickListener {
                myDialog.dismiss()
            }

            myDialog.show()
        }

        binding.dateEt.setOnClickListener {

            if (binding.emailEt.isFocusable)
                binding.emailEt.clearFocus()

            val calendar = Calendar.getInstance()

            Locale.setDefault(Locale("pt"));
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            calendar.timeInMillis = today
            val lastValidMonth = calendar.timeInMillis

            // Build constraints.
            val constraintsBuilder =
                CalendarConstraints.Builder()
                    .setEnd(lastValidMonth)

            val currentDate = LocalDate.now()
            val selectedMillis = currentDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

            val mtDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a sua data de nascimento")
                .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                .setCalendarConstraints(constraintsBuilder.build()) // Set the calendar constraints
                .setSelection(selectedMillis)
                .build()

            mtDatePicker.addOnPositiveButtonClickListener{ selection ->

                val selectedDate = Date(selection)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = formatter.format(selectedDate)

                binding.dateEt.setText(formattedDate)
            }

            mtDatePicker.show(parentFragmentManager,"DatePicker")
        }


        //-------------- VALIDATIONS --------------

        binding.firstNameEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                if (binding.firstNameEt.text.isNullOrEmpty()){
                    binding.firstNameTL.isErrorEnabled=true
                    binding.firstNameTL.error=getString(R.string.enter_first_name)
                    //toast(getString(R.string.enter_first_name))
                }else{
                    binding.firstNameTL.isErrorEnabled=false
                }
            }
        }

        binding.lastNameEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                if (binding.lastNameEt.text.isNullOrEmpty()){
                    binding.lastNameTL.isErrorEnabled=true
                    binding.lastNameTL.error=getString(R.string.enter_last_name)
                    // toast(getString(R.string.enter_last_name))
                }else{
                    binding.lastNameTL.isErrorEnabled=false
                }
            }
        }

        binding.emailEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                if (binding.emailEt.text.isNullOrEmpty()){
                    binding.emailTL.isErrorEnabled=true
                    binding.emailTL.error=getString(R.string.enter_email)
                }else if (!binding.emailEt.text.toString().isValidEmail()){
                    binding.emailTL.isErrorEnabled=true
                    binding.emailTL.error=getString(R.string.invalid_email)
                }else{
                    binding.emailTL.isErrorEnabled=false
                }
            }
        }

        binding.dateEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                if (binding.dateEt.text.isNullOrEmpty()){
                    binding.dateTL.isErrorEnabled=true
                    binding.dateTL.error=getString(R.string.enter_birthdate)
                }else{
                    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
                    try {
                        val dateTime: LocalDate = LocalDate.parse(binding.dateEt.text.toString(), formatter)
                        if (dateTime >= LocalDate.now()){
                            binding.dateTL.isErrorEnabled=true
                            binding.dateTL.error=getString(R.string.invalid_birthdate_2)
                        }else{
                            binding.dateTL.isErrorEnabled=false
                        }
                    }
                    catch (e: DateTimeException){
                        binding.dateTL.isErrorEnabled=true
                        binding.dateTL.error=getString(R.string.invalid_birthdate)
                    }
                }
            }else{
                val imm = binding.dateTL.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        binding.sexEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus){
                if (binding.sexEt.text.isNullOrEmpty()){
                    binding.sexTL.isErrorEnabled=true
                    binding.sexTL.error=getString(R.string.invalid_sex)
                }else{
                    binding.sexTL.isErrorEnabled=false
                }

            }else{
                val imm = binding.sexTL.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        binding.sexEt.addTextChangedListener {
            val tag= binding.imageView.tag
            if(tag == ImageTagsConstants.DEFAULT || tag == ImageTagsConstants.RANDOM_AVATAR) {
                val randAvatar = randomAvatarImg(binding.sexEt.text.toString())
                selectedAvatar = randAvatar.getName()
                binding.imageView.setImageResource(randAvatar.imgId)
                binding.imageView.tag=ImageTagsConstants.RANDOM_AVATAR
            }
        }

        binding.passEt.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus){
                if (binding.passEt.text.isNullOrEmpty()){
                    binding.passwordTL.isErrorEnabled=true
                    binding.passwordTL.error=getString(R.string.enter_password)
                    //toast(getString(R.string.enter_password))
                }else if (binding.passEt.text.toString().length < 8){
                    binding.passwordTL.isErrorEnabled=true
                    binding.passwordTL.error=getString(R.string.invalid_password_1)
                } else{
                    binding.passwordTL.isErrorEnabled=false
                }
            }
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



    private fun getUserRequest(): UserRequest {

        val sex: String = when (binding.sexEt.text.toString()) {
            "Masculino" -> SexConstants.M
            "Feminino" -> SexConstants.F
            else -> SexConstants.NA
        }

        var img = ""
        if (selectedAvatar != null){
            img = selectedAvatar!!
        }else if (fileName!=null){
            img= fileName!!
        }

        return UserRequest(
            name =  binding.firstNameEt.text.toString() + " "+ binding.lastNameEt.text.toString(),
            email = binding.emailEt.text.toString(),
            birth_date = binding.dateEt.text.toString(),
            password = binding.passEt.text.toString(),
            sex = sex,
            img_source=img
        )
    }

    private fun randomAvatarImg(sex: String): Avatar {

        when(sex) {
            "Masculino" -> return avatarArrayList[(0 until 5).random()]
            "Feminino" -> return avatarArrayList[(6 until 10).random()]
            else -> return avatarArrayList[(0 until 10).random()]
        }
    }

    private fun validation(): Boolean {

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

    @Deprecated("Deprecated in Java")
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
                .addOnSuccessListener {
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

            saveEditedImage()
        }
    }

    private fun saveEditedImage() {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, finalUri)
        saveMediaToStorage(bitmap)
    }

    private fun bindObservers() {
        authViewModel.userRegisterLiveData.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        Toast(context).showCustomToast (getString(R.string.user_registered_successfully), requireActivity(),ToastType.SUCCESS)
                        //toast(getString(R.string.user_registered_successfully))
                    }
                    is NetworkResult.Error -> {
                        Toast(context).showCustomToast (it.message.toString(), requireActivity(),ToastType.ERROR)
                        //toast(it.message.toString())
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
        binding.imageView.tag=ImageTagsConstants.FOTO
        selectedAvatar=null

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