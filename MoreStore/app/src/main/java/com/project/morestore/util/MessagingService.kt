package com.project.morestore.util

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.morestore.MainActivity
import com.project.morestore.R
import java.security.Permission
import java.security.Permissions

class MessagingService: FirebaseMessagingService() {
    private lateinit var broadcaster: LocalBroadcastManager


    override fun onCreate() {
        super.onCreate()
        broadcaster = LocalBroadcastManager.getInstance(baseContext)

    }

    override fun onNewToken(token: String) {

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("fire", "onMessageReceived ${message.data}")
        val orderid = message.data["id_type"]?.toLong()
        val changeOrderStatusText = message.data["text"]
        val changeOrderStatusTitle = message.data["title"]
        if(orderid != null){
            createOrderStatusChangeNotification(orderid, changeOrderStatusText.orEmpty(), changeOrderStatusTitle.orEmpty())
            return
        }
        val dialogId = message.data["message"]?.substringAfter(":")?.substringBefore("}")?.toLong()
        Log.d("fire", dialogId.toString())
        dialogId ?: return

        val intent = Intent(MESSAGE_INTENT).apply {
           putExtra(MESSAGE_KEY, dialogId  )
        }

        broadcaster.sendBroadcast(intent)

    }

    private fun createOrderStatusChangeNotification(orderId: Long, text: String, title: String){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(ORDER_KEY, orderId)
        }
        val pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, NotificationChannels.IMPORTANT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(this).notify(orderId.toInt(), notification)
    }



    companion object {
        const val MESSAGE_KEY = "message_key"
        const val MESSAGE_INTENT = "message_intent"
        const val ORDER_KEY = "id_type"
        const val NOTIFICATION_REQUEST_CODE = 123

    }
}