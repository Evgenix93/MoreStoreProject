package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PromoCode(
    val id: Long,
    val sum: Int,
    val code: String,
    val status: Int,
    val first_order: Int
)
