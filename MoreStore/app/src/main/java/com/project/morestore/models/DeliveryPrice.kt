package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeliveryPrice(
    val delivery_sum: Float,
    val period_min: Int,
    val period_max: Int,
    val calendar_min: Int,
    val calendar_max: Int,
    val total_sum: Float,
)
