package com.example.projectfoodmanager.data.api

import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {
    @POST("/user")
    suspend fun registerUser(@Body user : UserRequest): Response<UserResponse>

    @GET("/user")
    suspend fun getUser(@Query("uuid") userUUID: String): Response<UserResponse>
}