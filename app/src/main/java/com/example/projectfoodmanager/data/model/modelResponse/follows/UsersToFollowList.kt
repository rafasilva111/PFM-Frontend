package com.example.projectfoodmanager.data.model.modelResponse.follows
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata

data class UsersToFollowList(
    val _metadata: Metadata,
    val result: MutableList<UserToFollow>
)