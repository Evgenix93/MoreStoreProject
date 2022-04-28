package com.project.morestore.util

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.morestore.MainActivity
import com.project.morestore.models.*
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi

class MessagingService: FirebaseMessagingService() {
    private lateinit var broadcaster: LocalBroadcastManager

    override fun onCreate() {
        super.onCreate()
        broadcaster = LocalBroadcastManager.getInstance(baseContext)

    }

    override fun onNewToken(token: String) {

    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("fire", "onMessageReceived ${message.data}")

        /*val id: Long = message.data["id"]!!.toLong()

        val idSender: Long = message.data["id_sender"]!!.toLong()
       // @Json(name = "id_dialog")
        val idDialog: Long = message.data["id_dialog"]!!.toLong()
        val text: String? = message.data["text"]
        val date: Long = message.data["date"]!!.toLong()
        val is_read: Int = message.data["is_read"]!!.toInt()
        //val user: User,
        val photoAdapter = ProductPhotoJsonAdapter(Moshi.Builder().build())
        val photo = photoAdapter.fromJson(message.data["photo"]!!)
        val videoAdapter = ProductVideoJsonAdapter(Moshi.Builder().build())
        val video = videoAdapter.fromJson(message.data["video"]!!)
        //@Json(name = "buy_suggest)
        val messageActionAdapter = MessageActionSuggestJsonAdapter(Moshi.Builder().build())
        val buySuggest = messageActionAdapter.fromJson(message.data["buy_suggest"])

        //@Json(name = "price_suggest")
        val priceSuggest = messageActionAdapter.fromJson(message.data["price_suggest"])
        //@Json(name = "sale_suggest")
        val saleSuggest = messageActionAdapter.fromJson(message.data["sale_suggest"])

        val messageModel = MessageModel(
            id = id,
            idSender = idSender,
            idDialog = idDialog,
            date = date,
            is_read = is_read,
            text = text,
            user = User(-1, null, null, null, null, null, null, null),
            photo = ,
            video = null,
            buySuggest = null,
            priceSuggest = null,
            saleSuggest = null
        )

        val intent = Intent(MESSAGE_INTENT).apply {
            putExtra(MESSAGE_KEY, messageModel )
        }

        broadcaster.sendBroadcast(intent)*/





    }

    companion object {
        const val MESSAGE_KEY = "message_key"
        const val MESSAGE_INTENT = "message_intent"
    }
}