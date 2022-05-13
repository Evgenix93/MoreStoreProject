package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderId(
    @Json(name = "id_order")
    val idOrder: Long

)
