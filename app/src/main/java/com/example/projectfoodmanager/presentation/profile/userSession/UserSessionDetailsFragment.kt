package com.example.projectfoodmanager.presentation.profile.userSession

import android.app.Dialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.AvatarGVAdapter
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelRequest.user.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentSessionAccountProfileBinding
import com.example.projectfoodmanager.presentation.auth.register.RegisterFragment
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalDateTimeToServerTime
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToLocalDateTime
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.Helper.Companion.userIsNot12Old
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.yalantis.ucrop.UCrop
import java.io.*
import java.lang.ref.WeakReference
import java.time.DateTimeException
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class UserSessionDetailsFragment : Fragment() {

    /** binding */
    private lateinit var binding: FragmentSessionAccountProfileBinding

    /** viewModels */
    private val userViewModel by activityViewModels<UserViewModel>()

    /** variables */
    private val TAG: String = "SessionProfileFragment"
    private lateinit var user: User
    private var newUser: UserRequest = UserRequest()
    private lateinit var imagePickingActivityResultLauncher: ActivityResultLauncher<Intent>

    /** Image */
    var selectedAvatar: String? = null

    /** injects */
    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var sharedPreference: SharedPreference

    /**
     *  Android LifeCycle
     * */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindObservers()

        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentSessionAccountProfileBinding.inflate(layoutInflater)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()

        if (isOnline(requireView().context)) {
            userViewModel.getUserSession()
        }else{
            user = sharedPreference.getUserSession()
            loadUI()
        }

        super.onViewCreated(view, savedInstanceState)
    }

    /**
     *  General
     * */

    private fun loadUI(){
        /**
         * Info
         */

        /** PERSONAL DATA  */
        //Set Profile Image
        loadUserImage(binding.profileIV, user.imgSource)


        val name = user.name.split(" ")
        binding.firstNameEt.setText(name[0])
        binding.lastNameEt.setText(name[1])


        user.birthDate?.let {
            binding.dateEt.setText(formatServerTimeToDateString(it))
        }


        val genders = resources.getStringArray(R.array.default_gender_array)
        when(user.sex){
            SEX.M -> binding.sexEt.setText(genders[0], false)
            SEX.F-> binding.sexEt.setText(genders[1], false)
            else -> binding.sexEt.setText(genders[2], false)
        }

        /** BIOMETRIC DATA  */

        binding.heightEt.setText(user.height.toInt().toString())
        binding.weightEt.setText(user.weight.toInt().toString())

        when(user.activityLevel.toFloat()){
            1.2F -> binding.activityLevelRg.check(R.id.op1_RB)
            1.375F -> binding.activityLevelRg.check(R.id.op2_RB)
            1.465F -> binding.activityLevelRg.check(R.id.op3_RB)
            1.55F -> binding.activityLevelRg.check(R.id.op4_RB)
            1.725F -> binding.activityLevelRg.check(R.id.op5_RB)
            1.9F -> binding.activityLevelRg.check(R.id.op6_RB)
        }

        /** AUTH DATA  */
        binding.userNameET.setText(user.username)

        binding.emailET.setText(user.email)
        binding.emailET.isEnabled = false

/*
        binding.nameTV.text =  getString(R.string.full_name, user.name)

        if(!user.verified){
            binding.profileCV.foreground=null
            //binding.vipIV.visibility=View.INVISIBLE
        }

        binding.nFollowedsTV.text = user.followeds.toString()
        binding.nFollowersTV.text = user.followers.toString()

        */
/**
         * Image offline
         *//*


        Helper.loadUserImage(binding.profileIV, user.img_source)
*/

    }

    private fun setUI() {

        /**
         *  General
         * */
        val activity = requireActivity()
        Helper.changeMenuVisibility(false, activity)
        Helper.changeTheme(false, activity, context)

        binding.header.titleTV.text = "Meu Perfil"

        val genders = resources.getStringArray(R.array.gender_array)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_register_gender,genders)

        binding.sexEt.setAdapter(arrayAdapter)


        /**
         *  Notifications
         * */


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
                if (Helper.checkPermission(requireContext())) {
                    pickFromCamera()
                }else{
                    toast( "Allow all permissions")
                    Helper.requestPermission(requireActivity())
                }

                myDialog.dismiss()
            }

            galleryIV.setOnClickListener {
                if (Helper.checkPermission(requireContext())) {
                    pickFromGallery()
                }else{
                    toast( "Allow all permissions")
                    Helper.requestPermission(requireActivity())
                }
                myDialog.dismiss()
            }


            val avatarGV= dialogBinding.findViewById<GridView>(R.id.gvAvatar)

            val adapter = AvatarGVAdapter(requireContext(), Avatar.avatarArrayList)
            avatarGV.adapter = adapter

            avatarGV.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->

                val avatar= adapter.getItem(position)

                if (avatar!!.reserved){
                    Toast(context).showCustomToast ("Este avatar apenas esta disponivel para VIP!\n Registe-se e depois pode adquirir o VIP", requireActivity(),ToastType.VIP)
                }else{
                    // Handle the item selection here
                    selectedAvatar = avatar.getName()

                    Glide.with(this)
                        .load(avatar.imgId)
                        .into(binding.profileIV)

                    binding.profileIV.tag=ImageTagsConstants.SELECTED_AVATAR
                    myDialog.dismiss()

                }
            }


            cancelBtn.setOnClickListener {
                myDialog.dismiss()
            }

            myDialog.show()
        }

        binding.dateEt.setOnClickListener {

            val mtDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a sua data de nascimento")
                .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                .setSelection(formatServerTimeToLocalDateTime(user.birthDate!!)
                    .toInstant(ZoneOffset.UTC)
                    .toEpochMilli())
                .build()

            mtDatePicker.addOnPositiveButtonClickListener{ selection ->

                val selectedDate = Date(selection)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .atStartOfDay()

                user.birthDate = formatLocalDateTimeToServerTime(selectedDate)
                binding.dateEt.setText(formatLocalDateTimeToDateString(selectedDate))
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
                    binding.sexTL.error=getString(R.string.USER_ERROR_GENDER_INVALID)
                }else{
                    binding.sexTL.isErrorEnabled=false
                }

            }else{
                val imm = binding.sexTL.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        binding.activityLevelRg.setOnCheckedChangeListener { _, checkedId ->
            validateActivityLevel()
            when(checkedId){
                R.id.op1_RB-> newUser.activityLevel= 1.2
                R.id.op2_RB-> newUser.activityLevel= 1.375
                R.id.op3_RB-> newUser.activityLevel= 1.465
                R.id.op4_RB-> newUser.activityLevel= 1.55
                R.id.op5_RB-> newUser.activityLevel= 1.725
                R.id.op6_RB-> newUser.activityLevel= 1.9
            }

        }

        binding.weightEt.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val weightString = binding.weightEt.text.toString()
                val weight = weightString.toFloatOrNull()

                when {
                    weightString.isBlank() -> {
                        binding.weightTL.isErrorEnabled = true
                        binding.weightTL.error = getString(R.string.USER_ERROR_WEIGHT_INVALID)
                    }
                    weight == null || weight !in 30F..200F -> {
                        binding.weightTL.isErrorEnabled = true
                        binding.weightTL.error = getString(R.string.USER_ERROR_WEIGHT_INVALID_2)
                    }
                    else -> {
                        binding.weightTL.isErrorEnabled = false
                    }
                }

            }
        }

        binding.heightEt.setOnFocusChangeListener { _, hasFocus ->

            if (!hasFocus){
                val heightString = binding.heightEt.text.toString()
                var height = heightString.toFloatOrNull()
                if (height != null && height in 1.0..3.0){
                    height *= 100F
                    binding.heightEt.setText(height.toString())
                }


                when {
                    heightString.isBlank() -> {
                        binding.heightTL.isErrorEnabled = true
                        binding.heightTL.error = getString(R.string.USER_ERROR_HEIGHT_INVALID)
                    }
                    height == null || height !in 100.0..300.0 -> {
                        binding.heightTL.isErrorEnabled = true
                        binding.heightTL.error = getString(R.string.USER_ERROR_HEIGHT_INVALID_2)
                    }
                    else -> {
                        binding.heightTL.isErrorEnabled = false
                    }
                }
            }
        }

        /**
         * Buttons
         */

        binding.header.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        /** Update PersonalData  */
        binding.savePersonalData.setOnClickListener {

            binding.firstNameEt.clearFocus()
            binding.lastNameEt.clearFocus()
            binding.dateEt.clearFocus()
            binding.sexEt.clearFocus()

            if (validation(SelectedTab.PERSONAL_DATA))
                userViewModel.updateUser(patchUser(SelectedTab.PERSONAL_DATA))
        }

        /** Update BiometricData  */
        // toggle card visibility
        binding.biometricTV.setOnClickListener {
            slideUpDown(binding.biometricDataCL)
        }

        binding.saveBiometricData.setOnClickListener {

            binding.weightEt.clearFocus()
            binding.heightEt.clearFocus()

            if (validation(SelectedTab.BIOMETRIC_DATA))
                userViewModel.updateUser(patchUser(SelectedTab.BIOMETRIC_DATA))

        }

        /** Update AuthData  */
        // toggle card visibility
        binding.authDataTV.setOnClickListener {
            binding.userNameET.clearFocus()
            binding.passEt.clearFocus()
            binding.passEtConf.clearFocus()
            slideUpDown(binding.authDataCL)
        }


        binding.saveAuthData.setOnClickListener {
            binding.userNameET.clearFocus()
            binding.passEt.clearFocus()
            binding.passEtConf.clearFocus()
            if (validation(SelectedTab.AUTHENTICATION_DATA))
                userViewModel.updateUser(patchUser(SelectedTab.AUTHENTICATION_DATA))
        }

        /** Reset to Old Personal Data  */
        binding.cancelPersonalData.setOnClickListener {
            loadUI()
        }

        /** Reset to Old Biometric Data  */
        binding.cancelBiometricData.setOnClickListener {
            loadUI()
        }

        /** Reset to Old Auth Data  */
        binding.cancelAuthData.setOnClickListener {
            loadUI()
        }


    }

    private fun bindObservers() {
        userViewModel.userResponseLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        user = result.data!!

                        loadUI()
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }

        userViewModel.userUpdateResponseLiveData.observe(viewLifecycleOwner) { userSessionResponse ->
            userSessionResponse.getContentIfNotHandled()?.let { result->

                when (result) {
                    is NetworkResult.Success -> {
                        toast("Dados atualizados com sucesso")

                        user = result.data!!

                        loadUI()

                    }
                    is NetworkResult.Error -> {

                        toast("Dados não atualizados, alguma coisa se passou.",ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        // show loading bar
                        //todo falta aqui uma loading bar

                    }
                }
            }
        }


    }

    /**
     *  Functions
     * */

    private fun hideKeyboard() {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun slideUpDown(view: View) {
        hideKeyboard()
        if (!view.isVisible) {
            // Show Card
            view.visibility = View.VISIBLE
        } else {
            // Hide Card
            view.visibility = View.GONE
        }
    }

    private fun validation(selectedTab:String): Boolean {

        var isValid = true

        when(selectedTab){
            SelectedTab.PERSONAL_DATA -> {
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
                    binding.sexTL.error=getString(R.string.USER_ERROR_GENDER_INVALID)
                    isValid = false
                }
                else
                    binding.sexTL.isErrorEnabled=false


            }

            SelectedTab.BIOMETRIC_DATA -> {
                val heightString = binding.heightEt.text.toString()
                val height = heightString.toFloatOrNull()

                when {
                    heightString.isBlank() -> {
                        binding.heightTL.isErrorEnabled = true
                        binding.heightTL.error = getString(R.string.USER_ERROR_HEIGHT_INVALID)
                    }
                    height == null || (height !in 120.0..300.0 && height !in 1.20..3.0) -> {
                        binding.heightTL.isErrorEnabled = true
                        binding.heightTL.error = getString(R.string.USER_ERROR_HEIGHT_INVALID_2)
                    }
                    else -> {
                        if (height in 1.20..3.0)
                            binding.heightEt.setText((height * 100).toString())

                        binding.heightTL.isErrorEnabled = false
                    }
                }

                val weightString = binding.weightEt.text.toString()
                val weight = weightString.toFloatOrNull()

                when {
                    weightString.isBlank() -> {
                        binding.weightTL.isErrorEnabled = true
                        binding.weightTL.error = getString(R.string.USER_ERROR_WEIGHT_INVALID)
                        isValid = false
                    }
                    weight == null || weight !in 40F..150F -> {
                        binding.weightTL.isErrorEnabled = true
                        binding.weightTL.error = getString(R.string.USER_ERROR_WEIGHT_INVALID_2)
                        isValid = false
                    }
                    else -> {
                        binding.weightTL.isErrorEnabled = false
                    }
                }

                isValid = validateActivityLevel() and isValid
            }
            SelectedTab.AUTHENTICATION_DATA -> {
                //TODO
            }
        }

        return isValid
    }

    private fun validateActivityLevel():Boolean{

        var isValid = true
        if (binding.activityLevelRg.checkedRadioButtonId == -1) {
            isValid = false

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
            binding.errorActivityLevelTV.text=getString(R.string.USER_ERROR_ACTIVITY_LEVEL_INVALID)
        }else{
            binding.op1RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.op1RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op2RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.op2RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op3RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.op3RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op4RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.op4RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op5RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.op5RB.setTextColor(resources.getColor(R.color.black,null))
            binding.op6RB.buttonTintList=context?.resources?.getColorStateList(R.color.grey,null)
            binding.op6RB.setTextColor(resources.getColor(R.color.black,null))
            binding.errorActivityLevelTV.visibility=View.INVISIBLE
        }
        return isValid
    }

    private fun errorOnBirthdate(error: String){
        binding.dateTL.isErrorEnabled=true
        binding.dateTL.error=error
    }

    private fun patchUser(selectedTab: String): UserRequest {

        val userRequest = UserRequest()

        when(selectedTab){
            SelectedTab.PERSONAL_DATA -> {
                /** First Name  */
                userRequest.name = binding.firstNameEt.text.toString().trim() + " " + binding.lastNameEt.text.toString().trim()

                /** Birth Date  */
                userRequest.birth_date = binding.dateEt.text.toString()

                /** Sex  */
                val genders = resources.getStringArray(R.array.default_gender_array)
                when(binding.sexEt.text.toString()){
                    genders[0] -> userRequest.sex = SEX.M
                    genders[1] -> userRequest.sex = SEX.F
                    else -> userRequest.sex = null
                }

                /** Img Source  */
                userRequest.img_source = selectedAvatar

            }
            SelectedTab.BIOMETRIC_DATA -> {

                /** Activity Level  */
                userRequest.activityLevel = newUser.activityLevel

                /** Weight  */
                userRequest.weight =  binding.weightEt.text.toString().toFloat()

                /** Height  */
                userRequest.height = binding.heightEt.text.toString().toFloat()
            }
            SelectedTab.AUTHENTICATION_DATA -> {
                //TODO
            }
        }

        return userRequest
    }

    private fun showValidationErrors(error: String) {
        toast(String.format(resources.getString(R.string.txt_error_message, error)))
    }

    /**
     *  Images
     * */

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
            //uri= FileProvider.getUriForFile(Objects.requireNonNull(requireActivity().applicationContext),
            //    BuildConfig.APPLICATION_ID + ".provider", file);


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
        startActivityForResult(intent, ActionResultCodes.GALLERY_REQUEST_CODE)
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

    private fun setImage(uri: Uri){
        binding.profileIV.tag=ImageTagsConstants.FOTO
        RegisterFragment.selectedAvatar =null

        Glide.with(this)
            .load(uri)
            .into(binding.profileIV)
    }





    /**
     *  Object
     * */

    companion object {

        object SelectedTab {
            const val PERSONAL_DATA = "1"
            const val BIOMETRIC_DATA = "2"
            const val AUTHENTICATION_DATA = "3"
        }
    }

}