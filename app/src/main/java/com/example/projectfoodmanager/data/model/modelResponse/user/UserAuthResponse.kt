package com.example.projectfoodmanager.data.model.modelResponse.user

import com.example.projectfoodmanager.data.model.User

data class UserAuthResponse(
    val token: String,
    val user: User
)
