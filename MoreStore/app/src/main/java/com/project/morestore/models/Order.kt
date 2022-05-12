package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Order(
    val id: Long,
    val delivery: Int,
    val pay: Int,
    @Json(name = "id_cart")
    val idCart: List<Long>,
    val cart: List<Product>?,
    val status: Int,
    @Json(name = "place_address")
    val placeAddress: String?,
    val place: Int,
    val sum: Int
)
