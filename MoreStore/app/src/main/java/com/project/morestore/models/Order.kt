package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Order (
    val id: Long,
    val cart: List<Product>,
    @Json(name = "id_user")
    val idUser: Long,
    @Json(name = "id_seller")
    val idSeller: Long
)