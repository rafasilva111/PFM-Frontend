package com.example.projectfoodmanager.data.model.modelResponse.follows
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import com.example.projectfoodmanager.data.model.user.User

data class UserToFollow(
    var request_sent: Boolean,
    val user: User
)