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
        val id = message.data["id_type"]?.filter { it.isDigit() }?.toLong()
        val text = message.data["text"]
        val title = message.data["title"]
        val pushType = message.data["type"]
        if(id != null){
            createNotification(id, text.orEmpty(), title.orEmpty(), pushType.orEmpty())
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

    private fun createNotification(id: Long, text: String, title: String, pushType: String){
        Log.d("mylog", "IdFromNotification: $id")
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                                            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(PUSH_TYPE, pushType)
            putExtra(ID, id)

        }
        val pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, NotificationChannels.IMPORTANT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(this).notify(id.toInt(), notification)
    }



    companion object {
        const val MESSAGE_KEY = "message_key"
        const val MESSAGE_INTENT = "message_intent"
        const val ID = "id_type"
        const val NOTIFICATION_REQUEST_CODE = 123
        const val NEW_PRICE_PUSH = "order_new_price_wishlist"
        const val NEW_PRODUCT_PUSH = "order_new_wishlist"
        const val PRODUCT_SOLD_PUSH = "order_new_status_wishlist"
        const val ORDER_NEW_STATUS_PUSH = "order_new_status"
        const val PUSH_TYPE = "type"

    }
}