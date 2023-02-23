package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.util.Resource
import com.example.projectfoodmanager.util.UiState
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?

    //User
    suspend fun registerUser(user : UserRequest) : Resource<UserResponse>
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    suspend fun getUserSession() : Resource<User>
    fun logout(result: () -> Unit)

}