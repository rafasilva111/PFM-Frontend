package com.example.projectfoodmanager.data.api

import com.example.projectfoodmanager.data.model.modelResponse.auth.RefreshToken
import com.example.projectfoodmanager.data.model.modelResponse.user.auth.AuthToken
import com.example.projectfoodmanager.util.Constants
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
): Authenticator {


    override fun authenticate(route: Route?, response: Response): Request? = runBlocking {
        val refreshToken = tokenManager.getRefreshToken()
            ?: // If there's no refresh token, return null to stop retrying and avoid the recursive loop
            return@runBlocking null

        // Try to get a new token using the refresh token
        val newToken = getNewToken(refreshToken)

        if (!newToken.isSuccessful || newToken.body() == null) {
            // If token refresh fails, clear the session and return null
            tokenManager.deleteSession()
            return@runBlocking null
        }

        // If successful, save the new token and update the request with the new access token
        val newAccessToken = newToken.body()!!.accessToken
        tokenManager.saveToken(newToken.body()!!)
        return@runBlocking response.request().newBuilder()
            .header("Authorization", "Bearer $newAccessToken")
            .build()
    }

    private suspend fun getNewToken(refreshToken: String): retrofit2.Response<AuthToken> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(AuthApiInterface::class.java)
        return service.refreshToken(RefreshToken(refreshToken))
    }
}