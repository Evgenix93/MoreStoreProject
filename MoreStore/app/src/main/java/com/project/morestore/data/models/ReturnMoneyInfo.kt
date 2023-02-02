package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReturnMoneyInfo(
    val orderId: String,
    val amount: Int
)
