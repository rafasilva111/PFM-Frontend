package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.util.NetworkResult

interface AuthRepository {

    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
    val userLogoutResponseLiveData: LiveData<NetworkResult<String>>

    //User
    suspend fun registerUser(user : UserRequest)
    suspend fun loginUser(email: String, password: String)
    suspend fun logoutUser()


}