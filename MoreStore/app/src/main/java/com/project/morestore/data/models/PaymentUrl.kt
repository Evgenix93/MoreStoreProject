package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentUrl(
    @Json(name = "payment_url")
    val formUrl: String
)
