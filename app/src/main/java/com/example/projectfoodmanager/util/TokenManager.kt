package com.example.projectfoodmanager.util

import android.content.Context
import android.content.SharedPreferences
import com.example.projectfoodmanager.data.model.modelResponse.user.auth.AuthToken
import com.example.projectfoodmanager.util.Constants.PREFS_TOKEN_FILE
import com.example.projectfoodmanager.util.Session.ACCESS_TOKEN
import com.example.projectfoodmanager.util.Session.ACCESS_TOKEN_EXPIRES
import com.example.projectfoodmanager.util.Session.REFRESH_TOKEN
import com.example.projectfoodmanager.util.Session.REFRESH_TOKEN_EXPIRES
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveToken(authResponse: AuthToken) {
        val editor = prefs.edit()
        editor.putString(REFRESH_TOKEN, authResponse.refreshToken)
        editor.putString(REFRESH_TOKEN_EXPIRES, authResponse.refreshTokenExpires)
        editor.putString(ACCESS_TOKEN, authResponse.accessToken)
        editor.putString(ACCESS_TOKEN_EXPIRES, authResponse.accessTokenExpires)
        editor.apply()
    }

    fun getRefreshToken(): String? {

        val refreshToken = prefs.getString(REFRESH_TOKEN, null)
        val refreshTokenExpires = prefs.getString(REFRESH_TOKEN_EXPIRES, null)

        if (refreshToken != null && refreshTokenExpires != null){
            val tokenExpirationDate = Helper.formatServerTimeToLocalDateTime(refreshTokenExpires)

            // Get the current time
            val currentDateTime = LocalDateTime.now()

            // Check if the token has expired
            if (tokenExpirationDate.isAfter(currentDateTime)) {
                // Token is still valid
                return refreshToken
            }
        }


        return null
    }

    fun getAccessToken(): String? {

        val refreshToken = prefs.getString(ACCESS_TOKEN, null)
        val refreshTokenExpires = prefs.getString(ACCESS_TOKEN_EXPIRES, null)

        if (refreshToken != null && refreshTokenExpires != null){
            val tokenExpirationDate = Helper.formatServerTimeToLocalDateTime(refreshTokenExpires)

            // Get the current time
            val currentDateTime = LocalDateTime.now()

            // Check if the token has expired
            if (tokenExpirationDate.isAfter(currentDateTime)) {
                // Token is still valid
                return refreshToken
            }
        }

        return null
    }

    fun deleteAccessToken() {
        val editor = prefs.edit()
        editor.remove(REFRESH_TOKEN)
        editor.remove(REFRESH_TOKEN_EXPIRES)
        editor.apply()
    }
    fun deleteRefreshToken() {
        val editor = prefs.edit()
        editor.remove(ACCESS_TOKEN)
        editor.remove(ACCESS_TOKEN_EXPIRES)
        editor.apply()
    }

    fun deleteSession() {
        deleteAccessToken()
        deleteRefreshToken()
    }
}