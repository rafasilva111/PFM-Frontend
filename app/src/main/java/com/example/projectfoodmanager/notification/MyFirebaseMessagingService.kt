package com.example.projectfoodmanager.notification


import android.content.Intent
import android.util.Log
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private  val TAG = "MyFirebaseMessagingService"

    @Inject
    lateinit var mNotificationManager:MyNotificationManager


    companion object {
        const val ACTION_NOTIFICATION_RECEIVED = "com.example.projectfoodmanager.ACTION_NOTIFICATION_RECEIVED"
        const val EXTRA_NOTIFICATION_DATA = "extra_notification_data"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains data
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            // Handle your data message here
        }
        val teste = remoteMessage.notification

        // Check if the message contains a notification
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")

            // Handle your notification message here

            // Broadcast the notification data
            val intent = Intent(ACTION_NOTIFICATION_RECEIVED)
            intent.putExtra(EXTRA_NOTIFICATION_DATA, it.body) // You can put more data if needed
            sendBroadcast(intent)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }



}