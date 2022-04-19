package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyAddress(
    val id :Long,
    val type :Type = Type.DELIVERY,
    val phone :String,
    val address :DeliveryAddress,
    val name :String,
    @Json(name = "is_default")
    val favorite :Boolean = false
) {
    enum class Type { DELIVERY, PICKUP }
}

@JsonClass(generateAdapter = true)
data class MyAddressData(
    val name :String,
    val phone :String,
    val address :DeliveryAddress,
    @Json(name = "is_default")
    val favorite :Boolean = false
)