package com.project.morestore.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

object NotificationChannels {
    val IMPORTANT_CHANNEL_ID = "important_channel"

    fun create(context: Context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context){
        val name = "importantChannel"
        val priority = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(IMPORTANT_CHANNEL_ID, name, priority)

        NotificationManagerCompat.from(context).createNotificationChannel(channel)

    }
}