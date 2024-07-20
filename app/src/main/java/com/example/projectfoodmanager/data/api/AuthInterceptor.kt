package com.example.projectfoodmanager.data.api


import android.util.Log
import com.example.projectfoodmanager.util.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    @Inject
    lateinit var tokenManager: TokenManager
    private val TAG = "AuthInterceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val token = tokenManager.getAccessToken()
        Log.i(TAG, "intercept: user token: $token")
        request.addHeader("Authorization", "Bearer $token")
        return chain.proceed(request.build())
    }
}