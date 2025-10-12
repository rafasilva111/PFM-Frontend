package com.example.projectfoodmanager.di.notification


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.projectfoodmanager.MainActivity
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.notification.Notification
import com.example.projectfoodmanager.util.FragmentsToOpen
import java.util.*
import javax.inject.Inject


class MyNotificationManager @Inject constructor(private val mCtx: Application) {
    private val rand = Random()


    private val notificationChannel = NotificationChannel(
        "Channel_id_default", "Channel_name_default", NotificationManager.IMPORTANCE_HIGH
    )

    private val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


    fun textNotification(notification: Notification) {

        val idNotification = rand.nextInt(1000000000)

        val attributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        val notificationManager =  mCtx.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationChannel.description = "Channel_description_default"
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.setSound(soundUri, attributes)
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationBuilder = NotificationCompat.Builder(mCtx, "Channel_id_default")

        val notificationIntent = Intent(mCtx, MainActivity::class.java).apply {
            // Pass any data you need to identify which fragment to open
            putExtra("fragmentToOpen", FragmentsToOpen.FRAGMENT_COMMENTS)

            putExtra("object",notification)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            mCtx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        notificationBuilder.setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_logo)
            .setTicker(mCtx.resources.getString(R.string.app_name))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setContentIntent(pendingIntent)

        notificationManager.notify(idNotification, notificationBuilder.build())
    }


}