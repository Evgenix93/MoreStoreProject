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
    val text: String?,
    val date: Long,
    val is_read: Int,
    val user: User,
    val photo: List<ProductPhoto>?,
    val video: List<ProductVideo>?,
    @Json(name = "sale_suggest")
    val saleSuggest: MessageActionSuggest?,
    @Json(name = "buy_suggest")
    val buySuggest: MessageActionSuggest?,
    @Json(name = "price_suggest")
    val priceSuggest: MessageActionSuggest?
)
