package com.example.youtubeconverter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput

object NotificationHelper {
    const val CHANNEL_ID = "01"
    const val CHANNEL_NAME = "Ideas"
    const val NOTIFICATION_ID = 101
    const val KEY_TEXT_REPLY = "key_text_reply"

    fun createChannel(context: Context) {
        val manager = NotificationManagerCompat.from(context)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

        channel.description = "It's a personal channel"
        channel.enableVibration(false)
        channel.enableLights(true)
        channel.lightColor = Color.RED
        manager.createNotificationChannel(channel)
    }

    
    fun showNotification(context: Context) {
        // For insert link button
        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .build()
        val resultIntent = Intent(context, NotificationReceiver::class.java)
        val resultPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            resultIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        val replyAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_input_add,
            "Insert Link", resultPendingIntent)
            .addRemoteInput(remoteInput)
            .build()


        // For close button
        val closeIntent = Intent(context, CloseNotificationReceiver::class.java)
        val closePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            closeIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        val closeAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_notification_clear_all,
            "Close", closePendingIntent)
            .build()


        val notification = NotificationCompat.Builder(context, CHANNEL_ID).setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle("Paste the copied link here to download the .mp3")
            .setColor(Color.rgb(255, 159, 52))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .addAction(replyAction)
            .addAction(closeAction)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}