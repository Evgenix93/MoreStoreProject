package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PayOrderInfo(
    val sum: Int,
    val id_order: Long
)
