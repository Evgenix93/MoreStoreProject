package com.project.morestore.util

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

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
        Log.d("fire", "onMessageReceived ${message.data["message"]}")
        val dialogId = message.data["message"]?.substringAfter(":")?.substringBefore("}")?.toLong()
        Log.d("fire", dialogId.toString())
        dialogId ?: return

        val intent = Intent(MESSAGE_INTENT).apply {
           putExtra(MESSAGE_KEY, dialogId  )
        }

        broadcaster.sendBroadcast(intent)

    }

    companion object {
        const val MESSAGE_KEY = "message_key"
        const val MESSAGE_INTENT = "message_intent"
    }
}