package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YandexPriceCalculateInfo(
    val items: List<YandexItem>,
    @Json(name = "route_points")
    val routePoints: List<YandexPoint>
)

@JsonClass(generateAdapter = true)
data class YandexItem(
    val quantity: Int = 1,
    val size: YandexItemSize,
    val weight: Float
)

@JsonClass(generateAdapter = true)
data class YandexItemSize(
    val height: Float,
    val length: Float,
    val width: Float
)

@JsonClass(generateAdapter = true)
data class YandexPoint(
    val coordinates: List<Double>
)
