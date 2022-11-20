package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PayOrderInfo(
    val sum: Float,
    val id_order: Long
)
