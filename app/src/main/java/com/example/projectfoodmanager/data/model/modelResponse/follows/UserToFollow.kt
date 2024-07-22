package com.example.projectfoodmanager.data.model.modelResponse.follows
import com.example.projectfoodmanager.data.model.modelResponse.user.User

data class UserToFollow(
    var request_sent: Boolean,
    var follower: Boolean,
    val user: User
)