package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelResponse.notifications.PushNotification
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import okhttp3.ResponseBody

interface NotificationRepository {

    val functionPostNotification: LiveData<Event<NetworkResult<ResponseBody>>>

    suspend fun postNotification(notificationModel: PushNotification)
}