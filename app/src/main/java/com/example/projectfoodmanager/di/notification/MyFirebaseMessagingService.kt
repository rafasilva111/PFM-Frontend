package com.example.projectfoodmanager.di.notification


import android.content.Intent
import android.util.Log
import com.example.projectfoodmanager.data.model.notification.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMessagingServ"

    @Inject
    lateinit var mNotificationManager: MyNotificationManager

    @Inject
    lateinit var gson: Gson

    companion object {
        const val ACTION_NOTIFICATION_RECEIVED = "com.example.projectfoodmanager.ACTION_NOTIFICATION_RECEIVED"
        const val EXTRA_NOTIFICATION_DATA = "extra_notification_data"
    }

    override fun onNewToken(s: String) {
        super.onNewToken(s)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data Payload: " + remoteMessage.data)


            val notificationData = remoteMessage.data.toNotification()
           mNotificationManager.textNotification(notificationData)

            sendBroadcast(Intent(ACTION_NOTIFICATION_RECEIVED).apply {
                putExtra(EXTRA_NOTIFICATION_DATA, notificationData)
            })


        }
    }

    // Extension function to convert a Map<String, String> to a Notification object
    private fun Map<String, String>.toNotification(): Notification {
        return gson.fromJson(this["notification"]?: "", Notification::class.java)
    }
}