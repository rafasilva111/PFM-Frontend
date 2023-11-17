package com.example.projectfoodmanager.data.model.modelResponse.notifications

import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import com.example.projectfoodmanager.data.model.modelResponse.user.User

data class NotificationList(
    val _metadata: Metadata,
    val result: MutableList<Notification>
)