package com.example.projectfoodmanager.data.model.modelResponse.user


data class UserAuthResponse(
    val refresh_token: String,
    val refresh_token_expires: String,
    val access_token: String,
    val access_token_expires: String,
)
