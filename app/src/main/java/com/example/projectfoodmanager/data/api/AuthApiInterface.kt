package com.example.projectfoodmanager.data.api

import com.example.projectfoodmanager.data.model.modelResponse.auth.RefreshToken
import com.example.projectfoodmanager.data.model.modelResponse.user.auth.AuthToken
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiInterface {

    @POST("${ApiInterface.API_V1_BASE_URL}/auth/refresh")
    suspend fun refreshToken(@Body refresh: RefreshToken): Response<AuthToken>
}