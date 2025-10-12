package com.example.projectfoodmanager.data.model.user
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import com.example.projectfoodmanager.data.model.modelResponse.user.User

data class UserList(
    val _metadata: Metadata,
    val result: MutableList<User>
)