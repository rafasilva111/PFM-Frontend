package com.example.projectfoodmanager.notification


import android.content.Intent
import android.util.Log
import com.example.projectfoodmanager.util.FirebaseNotificationCode
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMessagingServ"

    @Inject
    lateinit var mNotificationManager: MyNotificationManager

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
            try {

                val notificationData = HashMap<String, String>()
                notificationData["title"] = remoteMessage.data["title"] ?: ""
                notificationData["message"] = remoteMessage.data["message"] ?: ""
                notificationData["type"] = remoteMessage.data["type"] ?: ""

                mNotificationManager.textNotification(notificationData["title"], notificationData["message"])

                val intent = Intent(ACTION_NOTIFICATION_RECEIVED)
                intent.putExtra(EXTRA_NOTIFICATION_DATA, notificationData) // You can put more data if needed
                sendBroadcast(intent)

            } catch (e: Exception) {
                Log.d(TAG, "Exception: " + e.message)
            }
        }
    }
}