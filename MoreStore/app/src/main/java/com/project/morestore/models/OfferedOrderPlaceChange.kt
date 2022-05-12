package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OfferedOrderPlaceChange(
    @Json(name = "id_order")
    val idOrder: Long,
    @Json(name = "id_address")
    val idAddress: Long,
val address: String,
val status: Int

)
