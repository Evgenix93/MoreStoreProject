package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageModel(
    val id: Long,
    @Json(name = "id_sender")
    val idSender: Long,
    @Json(name = "id_dialog")
    val idDialog: Long,
    val text: String,
    val date: Long,
    val is_read: Int,
    val user: User
)
