package com.example.projectfoodmanager.util

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.projectfoodmanager.BuildConfig
import java.io.*
import java.text.SimpleDateFormat

class Helper {
    companion object {




        fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun hideKeyboard(view: View){
            try {
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }catch (e: Exception){

            }
        }

        fun formatNameToNameUpper(name: String):String{

            return name.split(' ').joinToString(" ") { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()) else it.toString() } }
        }

        // server string -> localTimeDate
        fun formatServerTimeToLocalDateTime(localDateTimeString: String): LocalDateTime{
            return LocalDateTime.parse(localDateTimeString, DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
        }

        // server string -> date string
        fun formatServerTimeToDateString(localDateTimeString: String): String{
            return localDateTimeString.split("T")[0]
        }

        fun formatLocalTimeToServerTime(localTime: LocalDateTime): String{
            return localTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
        }


        fun formatLocalDateToFormatDate(localTime: LocalDateTime): String{
            return localTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }

        fun formatLocalDateToFormatDate(localDate: LocalDate): String{
            return localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }

        fun formatLocalTimeToFormatTime(localTime: LocalDateTime): String{
            return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }


        fun isOnline(context: Context): Boolean {
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

        fun loadUserImage(imgAuthorIV: ImageView, imgSource: String) {
            imgAuthorIV.setImageResource(R.drawable.img_profile)


            if (imgSource.isNotEmpty())
                if (imgSource.contains("avatar")){
                    val avatar= Avatar.getAvatarByName(imgSource)
                    imgAuthorIV.setImageResource(avatar!!.imgId)

                }else{
                    val imgRef = Firebase.storage.reference.child(imgSource)
                    imgRef.downloadUrl.addOnSuccessListener { Uri ->
                        Glide.with(imgAuthorIV.context).load(Uri.toString()).into(imgAuthorIV)
                    }
                    .addOnFailureListener {
                        imgAuthorIV.setImageResource(R.drawable.img_profile)
                    }
                }

        }



        fun getStartAndEndOfWeek(date: LocalDate): Pair<LocalDate, LocalDate> {
            val startOfWeek = date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
            val endOfWeek = date.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
            return Pair(startOfWeek, endOfWeek)
        }

        fun getStartAndEndOfMonth(date: LocalDate): Pair<LocalDate, LocalDate> {
            val startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth())
            val endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth())
            return Pair(startOfMonth, endOfMonth)
        }

        fun loadRecipeImage(recipeIV: ImageView, imgSource: String) {
            val imgRef = Firebase.storage.reference.child(imgSource)
            imgRef.downloadUrl.addOnSuccessListener { Uri ->
                val imageURL = Uri.toString()
                Glide.with(recipeIV.context).load(imageURL).into(recipeIV)
            }
            .addOnFailureListener {
                recipeIV.setImageResource(R.drawable.default_image_recipe)
            }

        }

        fun closeKeyboard(activity: FragmentActivity,view: View) {
            (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
        }

        var STATUS_BAR_COLOR: Boolean? = null // true for mainColor (red), false for secondary (white)
        var MENU_VISIBILITY: Boolean? = null // true for visible, false for gone

        fun changeStatusBarColor(mainColor: Boolean, activity: FragmentActivity?, context: Context?){
            val window = activity?.window
            if (window != null && context != null) {
                if (mainColor) {
                    if (STATUS_BAR_COLOR != true){
                        //BACKGROUND in NAVIGATION BAR

                        window.statusBarColor = context.getColor(R.color.main_color)
                        window.navigationBarColor = context.getColor(R.color.main_color)
                        @Suppress("DEPRECATION")
                        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()  //set status text  light
                        STATUS_BAR_COLOR = true
                    }
                } else {
                    if (STATUS_BAR_COLOR != false) {
                        //BACKGROUND in NAVIGATION BAR
                        window.statusBarColor = context.getColor(R.color.background_1)


                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR //set status text dark

                        STATUS_BAR_COLOR = false
                    }
                }
            }

        }

        fun changeMenuVisibility(visibility: Boolean, activity: FragmentActivity?) {

            val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

            if (visibility ) {
                if((MENU_VISIBILITY != true)){
                    menu!!.visibility = View.VISIBLE
                    MENU_VISIBILITY = true
                }

            } else {
                if((MENU_VISIBILITY != false)){
                    menu!!.visibility = View.GONE
                    MENU_VISIBILITY = false
                }
            }
        }

        fun updateSystemBarsAppearance(activity: FragmentActivity,context: Context) {
            val window = activity.window

            // set bottom bar color
            window.navigationBarColor = context.getColor(R.color.main_color)

            // Set background color for status and navigation bars
            window.statusBarColor = context.getColor(R.color.background_1)

            // Set text color for status and navigation bars (for Android R and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = 0
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }


        fun checkPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission(activity: FragmentActivity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE,
                    CAMERA
                ),
                100
            )
        }

        /**
         *  Images
         * */
        fun randomAvatarImg(sex: String): Avatar {

            return when(sex) {
                "Masculino" -> Avatar.avatarArrayList[(0 until 5).random()]
                "Feminino" -> Avatar.avatarArrayList[(6 until 10).random()]
                else -> Avatar.avatarArrayList[(0 until 10).random()]
            }
        }



        /**
         *  Validation
         * */
        fun userIsNot12Old(dateString: String): Boolean {
            // Define the date format
            val dateFormat = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())

            try {
                // Parse the date string to a Date object
                val dateOfBirth = dateFormat.parse(dateString)

                // Calculate the age
                val currentDate = Calendar.getInstance().time

                val cal = Calendar.getInstance()
                cal.time = dateOfBirth ?: currentDate
                val birthYear = cal.get(Calendar.YEAR)

                cal.time = currentDate
                val currentYear = cal.get(Calendar.YEAR)

                val age = currentYear - birthYear

                // Check if the age is 12 or more
                return age <= 12
            } catch (e: Exception) {
                // Handle parsing exceptions
                e.printStackTrace()
            }

            // Return false in case of errors
            return true
        }
    }
}