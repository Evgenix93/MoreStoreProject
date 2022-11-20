package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YandexGoOrderInfo(
    val body: YandexOrderInfoBody

)

@JsonClass(generateAdapter = true)
data class YandexOrderInfoBody(
    val id: String,
    val status: String,
    val pricing: YandexPricing

)

@JsonClass(generateAdapter = true)
data class YandexPricing(
    val offer: YandexOffer?


)

@JsonClass(generateAdapter = true)
data class YandexOffer(
    val price: Float

)

