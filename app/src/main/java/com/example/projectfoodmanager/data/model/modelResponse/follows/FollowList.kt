package com.example.projectfoodmanager.data.model.modelResponse.follows
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import com.example.projectfoodmanager.data.model.modelResponse.user.User

data class FollowList(
    val _metadata: Metadata,
    val result: MutableList<User>
)