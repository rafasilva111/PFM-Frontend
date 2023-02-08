package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.util.Resource
import com.google.firebase.auth.FirebaseUser
import retrofit2.Response

interface AuthRepository {
    val currentUser: FirebaseUser?

    //User
    suspend fun registerUser(user : UserRequest) : Resource<UserResponse>
    suspend fun loginUser(email: String, password: String) : Resource<UserResponse>
    suspend fun getUser() : Resource<User>
    suspend fun logout(): Resource<UserResponse>

    //Recipe
}