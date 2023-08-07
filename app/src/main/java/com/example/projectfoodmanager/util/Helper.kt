package com.example.projectfoodmanager.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Avatar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

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

        fun formatLocalTimeToServerTime(localTime: LocalDateTime): String{
            return localTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss"))
        }

        fun formatLocalDateTimeToServerLocalDateTime(localTime: LocalDateTime): LocalDateTime{
            return LocalDateTime.parse(localTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")))
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
            if (imgSource.contains("avatar")){
                val avatar= Avatar.getAvatarByName(imgSource)
                imgAuthorIV.setImageResource(avatar!!.imgId)

            }else{
                val imgRef = Firebase.storage.reference.child("${FireStorage.user_profile_images}${imgSource}")
                imgRef.downloadUrl.addOnSuccessListener { Uri ->
                    Glide.with(imgAuthorIV.context).load(Uri.toString()).into(imgAuthorIV)
                }
                    .addOnFailureListener {
                        Glide.with(imgAuthorIV.context)
                            .load(R.drawable.img_profile)
                            .into(imgAuthorIV)
                    }
            }
        }


    }

}