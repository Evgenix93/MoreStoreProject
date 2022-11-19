package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PromoCode(
    val id: Long,
    val sum: Int,
    val code: String,
    val status: Int,
    @Json(name = "first_order")
    val firstOrder: Int
)
