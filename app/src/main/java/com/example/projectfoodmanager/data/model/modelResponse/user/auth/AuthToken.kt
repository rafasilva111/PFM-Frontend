package com.example.projectfoodmanager.data.model.modelResponse.user.auth

import com.google.gson.annotations.SerializedName


data class AuthToken(
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("refresh_token_expires")
    val refreshTokenExpires: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("access_token_expires")
    val accessTokenExpires: String,
)
