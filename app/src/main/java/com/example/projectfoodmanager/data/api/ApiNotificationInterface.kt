package com.example.projectfoodmanager.data.api

import com.example.projectfoodmanager.data.model.modelResponse.notifications.PushNotification
import com.example.projectfoodmanager.util.FirebaseNotifications.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiNotificationInterface {


    @Headers("Authorization: key=$SERVER_KEY","Content-Type:application/json")
    @POST("fcm/send")
    suspend fun sendNotification( @Body notificationModel: PushNotification): Response<ResponseBody>
}