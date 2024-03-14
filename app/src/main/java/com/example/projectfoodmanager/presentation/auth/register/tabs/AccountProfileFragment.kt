package com.example.projectfoodmanager.presentation.auth.register.tabs


import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.AvatarGVAdapter
import com.example.projectfoodmanager.BuildConfig
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar.Companion.avatarArrayList
import com.example.projectfoodmanager.databinding.FragmentRegisterAccountProfileBinding
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment.Companion.imgURI
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment.Companion.selectedAvatar
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.ActionResultCodes.GALLERY_REQUEST_CODE
import com.example.projectfoodmanager.util.Helper.Companion.checkPermission
import com.example.projectfoodmanager.util.Helper.Companion.randomAvatarImg
import com.example.projectfoodmanager.util.Helper.Companion.requestPermission
import com.example.projectfoodmanager.util.Helper.Companion.userIsNot12Old
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
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
import javax.inject.Inject


@AndroidEntryPoint
class AccountProfileFragment(private var parentBinding: FragmentRegisterBinding) : Fragment() {


    /** binding */
    private lateinit var binding: FragmentRegisterAccountProfileBinding

    /** viewModels */

    /** variables */
    private val TAG: String = "RegisterFragment"


    private lateinit var imagePickingActivityResultLauncher: ActivityResultLauncher<Intent>

    /** injects */

    @Inject
    lateinit var sharedPreference: SharedPreference

    /** adapters */



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentRegisterAccountProfileBinding.inflate(layoutInflater)
        }

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUI() {

        /**
         *  General
         * */

        binding.continueBtn.visibility = View.VISIBLE

        /** Sex Dropdown choices */
        binding.sexEt.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_register_gender,resources.getStringArray(R.array.gender_array)))




        /**
         *  Navigation
         * */

        binding.continueBtn.setOnClickListener {
            if (validation()){
                patchUser()
                val currentTab = parentBinding.fragmentRegisterTabLayout.selectedTabPosition
                val nextTab = currentTab + 1
                parentBinding.fragmentRegisterViewPager.currentItem= nextTab

            }
        }


        /**
         *  Pick Image
         * */

        /** Activity Result */

        imagePickingActivityResultLauncher  =
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

        /** Select image button  */

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
                if (checkPermission(requireContext())) {
                    pickFromCamera()
                }else{
                    toast( "Allow all permissions")
                    requestPermission(requireActivity())
                }

                myDialog.dismiss()
            }

            galleryIV.setOnClickListener {
                if (checkPermission(requireContext())) {
                    pickFromGallery()
                }else{
                    toast( "Allow all permissions")
                    requestPermission(requireActivity())
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
            binding.dateEt.isEnabled = false
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
                binding.dateEt.isEnabled = true
            }
            mtDatePicker.addOnCancelListener {  binding.dateEt.isEnabled = true}

            mtDatePicker.show(parentFragmentManager,"DatePicker")
        }


        /**
         *  Validations
         * */


        binding.firstNameEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                if (binding.firstNameEt.text.isNullOrEmpty()){
                    binding.firstNameTL.isErrorEnabled=true
                    binding.firstNameTL.error=getString(R.string.enter_first_name)
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
                }else{
                    binding.lastNameTL.isErrorEnabled=false
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



    }

    private fun validation(): Boolean {

        var isValid = true

        /** First Name  */

        if (binding.firstNameEt.text.isNullOrEmpty()){
            binding.firstNameTL.isErrorEnabled=true
            binding.firstNameTL.error=getString(R.string.enter_first_name)
            isValid = false
        }
        else
            binding.firstNameTL.isErrorEnabled=false

        /** Last Name  */
        if (binding.lastNameEt.text.isNullOrEmpty()){
            binding.lastNameTL.isErrorEnabled=true
            binding.lastNameTL.error=getString(R.string.enter_last_name)
            isValid = false
        }
        else
            binding.lastNameTL.isErrorEnabled=false

        /** Birth Date  */
        if(binding.dateEt.text.isNullOrEmpty()) {
            errorOnBirthdate(getString(R.string.enter_birthdate))
            isValid = false
        }
        else if (userIsNot12Old(binding.dateEt.text.toString())) {
            errorOnBirthdate(getString(R.string.enter_birthdate_error))
            isValid = false
        }
        else
            binding.dateTL.isErrorEnabled=false

        /** Sex  */
        if(binding.sexEt.text.isNullOrEmpty()) {
            binding.sexTL.isErrorEnabled=true
            binding.sexTL.error=getString(R.string.invalid_sex)
            isValid = false
        }
        else
            binding.sexTL.isErrorEnabled=false



        return isValid
    }

    private fun errorOnBirthdate(error: String){
        binding.dateTL.isErrorEnabled=true
        binding.dateTL.error=error
    }

    private fun patchUser() {

        /** First Name  */
        RegisterFragment.user.name = binding.firstNameEt.text.toString().trim() + " " + binding.lastNameEt.text.toString().trim()

        /** Birth Date  */
        RegisterFragment.user.birth_date = binding.dateEt.text.toString()

        /** Sex  */
        val genders = resources.getStringArray(R.array.gender_array)
        when(binding.sexEt.text.toString()){
            genders[0] -> RegisterFragment.user.sex = SEX.M
            genders[1] -> RegisterFragment.user.sex = SEX.F
            else -> RegisterFragment.user.sex = SEX.NA
        }

        /** Img Source  */
        RegisterFragment.user.img_source = selectedAvatar

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
            imgURI = UCrop.getOutput(data!!)

            setImage(imgURI!!)

            /** Save edited image */
            saveMediaToStorage(MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imgURI))
        }
    }

    private fun launchImageCrop(uri: Uri) {

        val destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        val options: UCrop.Options= UCrop.Options()
        options.setCropGridColor(Color.TRANSPARENT)
        options.setStatusBarColor(resources.getColor(R.color.main_color))

        startActivityForResult(
            UCrop.of(Uri.parse(uri.toString()), Uri.fromFile(File(requireContext().cacheDir,destination)))
                .withOptions(options)
                .withAspectRatio(3F, 4F)
                .useSourceImageAspectRatio()
                .withMaxResultSize(2000, 2000).getIntent(requireContext()), UCrop.REQUEST_CROP);
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
        imagePickingActivityResultLauncher.launch(intent)
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




}