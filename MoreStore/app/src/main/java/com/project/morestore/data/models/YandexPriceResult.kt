package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YandexPriceResult(
    val price: String
)

@JsonClass(generateAdapter = true)
data class YandexGoErrorEntity(
    val message: YandexGoErrorMessage
)

@JsonClass(generateAdapter = true)
data class YandexGoErrorMessage(
    val message: String
)
