package com.example.projectfoodmanager.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.lang.Exception
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

        fun formatLocalTimeToServerTime(localTime: LocalDateTime): String{
            return localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
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
    }

}