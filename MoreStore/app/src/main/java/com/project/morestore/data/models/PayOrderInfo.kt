package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PayOrderInfo(
    val sum: Float,
    val id_order: Long,
    @Json(name = "sum_payout")
    val sumPayout: Float
)
