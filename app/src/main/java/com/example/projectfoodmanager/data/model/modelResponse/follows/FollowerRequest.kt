package com.example.projectfoodmanager.data.model.modelResponse.follows

import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.google.gson.annotations.SerializedName

data class FollowerRequest(
    var id: Int,
    val follower: User,
    val followed: User,
    @SerializedName("created_at")
    val createdDate: String
)
