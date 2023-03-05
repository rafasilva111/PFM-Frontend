package com.example.projectfoodmanager.data.model.modelResponse

import com.example.projectfoodmanager.data.model.User
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserResponse(
    val token: String,
    val user: User
)
