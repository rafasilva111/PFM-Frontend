package com.example.projectfoodmanager.data.model.modelResponse.user
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata

data class UserList(
    val _metadata: Metadata,
    val result: MutableList<User>
)