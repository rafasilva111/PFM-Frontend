package com.example.projectfoodmanager.data.model.modelResponse.follows
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.google.gson.annotations.SerializedName

data class UserToFollow(
    @SerializedName("request_sent")
    var requestSent: Boolean,
    var follower: Boolean,
    val user: User
)