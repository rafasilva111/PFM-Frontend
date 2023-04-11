package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface AuthRepository {

    val userRegisterLiveData: LiveData<Event<NetworkResult<String>>>
    val userAuthResponseLiveData: LiveData<Event<NetworkResult<UserAuthResponse>>>
    val userLogoutResponseLiveData: LiveData<Event<NetworkResult<String>>>
    val userOldLiveData: LiveData<Event<NetworkResult<User>>>

    //User
    suspend fun registerUser(user : UserRequest)
    suspend fun loginUser(email: String, password: String)
    suspend fun getUserSession()
    suspend fun logoutUser()


}