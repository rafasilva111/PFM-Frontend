package com.example.projectfoodmanager.presentation.profile

import android.Manifest
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
import android.widget.AdapterView
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.AvatarGVAdapter
import com.example.projectfoodmanager.BuildConfig
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentProfileBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.FireStorage.user_profile_images
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.actionResultCodes.GALLERY_REQUEST_CODE
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.yalantis.ucrop.UCrop

import dagger.hilt.android.AndroidEntryPoint
import java.io.*
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentProfileBinding

    // viewModels
    private val authViewModel by activityViewModels<AuthViewModel>()

    // constants
    private val TAG: String = "ProfileFragment"
    private var fileName: String? = null
    private var selectedAvatar: String? = null
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var finalUri: Uri

    // injects
    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
        binding.logoutIB.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_logout)
                .setTitle("Logout")
                .setMessage(resources.getString(R.string.logout_confirmation_description))
                .setPositiveButton("Sim") { dialog, which ->
                    // Adicione aqui o código para apagar o registro
                    authViewModel.logoutUser()
                }
                .setNegativeButton("Não") { dialog, which ->
                    // Adicione aqui o código para cancelar a exclusão do registro
                    dialog.dismiss()
                }
                .show()

        }

        binding.shoppingLists.setOnClickListener {
            changeMenuVisibility(false,activity)
            findNavController().navigate(R.id.action_profileFragment_to_shoppingListListingFragment)
        }

        binding.myRecipes.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_favorites,Bundle().apply {
                putString("chip",getString(R.string.tab_created))
            })
        }

        binding.settingsCV.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }



        val userSession: User = sharedPreference.getUserSession()

        // set layout data before internet verification

        binding.nameTV.text =  getString(R.string.full_name, userSession.name)

        if(userSession.user_type != "V"){
            binding.profileCV.foreground=null
            binding.vipIV.visibility=View.INVISIBLE
        }

        binding.nFollowedsTV.text = userSession.followeds.toString()
        binding.nFollowersTV.text = userSession.followers.toString()
        //TODO: nRecipe created by user
        //binding.nRecipesTV.text = userSession.recipes.toString()


        binding.followedsLL.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followerFragment,Bundle().apply {
                putInt("userID",-1)
                putInt("followType",FollowType.FOLLOWEDS)
                putString("userName",userSession.name)
            })
            changeMenuVisibility(false,activity)
        }

        binding.followersLL.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_followerFragment,Bundle().apply {
                putInt("userID",-1)
                putInt("followType",FollowType.FOLLOWERS)
                putString("userName",userSession.name)
            })
            changeMenuVisibility(false,activity)
        }
        // load profile image offline

        if (isOnline(view.context)) {
            // load profile image online

            //-> Load Author img
            Helper.loadUserImage(binding.profileIV, userSession.img_source)

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

                val adapter = AvatarGVAdapter(requireContext(), Avatar.avatarArrayList)
                avatarGV.adapter = adapter

                avatarGV.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->

                    val avatar= adapter.getItem(position)

                    // Handle the item selection here
                    selectedAvatar = avatar!!.getName()
                    authViewModel.updateUser(UserRequest(img_source = selectedAvatar))

                    binding.profileIV.setImageResource(avatar.imgId)

                    myDialog.dismiss()

                }

                cancelBtn.setOnClickListener {
                    myDialog.dismiss()
                }

                myDialog.show()
            }

        }

    }

    override fun onStart() {

        changeStatusBarColor(true,activity,context)
        changeMenuVisibility(true,activity)

        super.onStart()
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
                        Glide.with(binding.profileIV.context)
                            .load(imageURL)
                            .into(binding.profileIV)
                    }
                }
                .addOnFailureListener {
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

    private fun launchImageCrop(uri: Uri) {


        var destination:String=StringBuilder(UUID.randomUUID().toString()).toString()
        var options: UCrop.Options=UCrop.Options()
        options.setCropGridColor(Color.TRANSPARENT)
        options.setStatusBarColor(resources.getColor(R.color.main_color))

        startActivityForResult(UCrop.of(Uri.parse(uri.toString()), Uri.fromFile(File(requireContext().cacheDir,destination)))
            .withOptions(options)
            .withAspectRatio(3F, 4F)
            .useSourceImageAspectRatio()
            .withMaxResultSize(2000, 2000).getIntent(requireContext()),UCrop.REQUEST_CROP);



    }

    private fun showValidationErrors(error: String) {
        toast(String.format(resources.getString(R.string.txt_error_message, error)))
    }

    private fun bindObservers() {
        authViewModel.userLogoutResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        tokenManager.deleteToken()
                        sharedPreference.deleteUserSession()
                        toast("Logout feito com sucesso!")
                        findNavController().navigate(R.id.action_profile_to_login)
                        changeMenuVisibility(false,activity)
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        })

        authViewModel.userUpdateResponseLiveData.observe(viewLifecycleOwner, Observer { userSessionResponse ->
            userSessionResponse.getContentIfNotHandled()?.let{

                when (it) {
                    is NetworkResult.Success -> {
                        toast("Dados atualizados com sucesso")

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

    // todo look into this
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
        selectedAvatar=null

        Glide.with(this)
            .load(uri)
            .into(binding.profileIV)
    }

}