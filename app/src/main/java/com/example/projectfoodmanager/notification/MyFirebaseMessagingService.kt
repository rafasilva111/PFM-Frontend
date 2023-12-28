package com.example.projectfoodmanager.notification


import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private  val TAG = "MyFirebaseMessagingServ"

    @Inject
    lateinit var mNotificationManager:MyNotificationManager



    override fun onNewToken(s: String) {
        super.onNewToken(s)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data Payload: " + remoteMessage.data)
            try {

                val title = remoteMessage.data["title"]
                val message = remoteMessage.data["message"]

                mNotificationManager.textNotification(title, message)

            } catch (e: Exception) {
                Log.d(TAG, "Exception: " + e.message)
            }
        }

    }



}