package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CdekOrder(
    val tariff_code: Int,
    val id_order: Long,
    val shipment_point1: String,
    val delivery_point1: String? = null,
    val sender: CdekSender,
    val recipient: CdekRecipient,
    val packages: CdekPackages,
    val items: CdekItems,
    @Json(name = "to_location")
    val toLocation: CdekLocation? = null

)

@JsonClass(generateAdapter = true)
data class CdekSender(
    val company: String,
    val name: String,
    val email: String,
    val phones: List<CdekPhone>,

)

@JsonClass(generateAdapter = true)
data class CdekRecipient(
    val name: String,
    val phones: List<CdekPhone>
)

@JsonClass(generateAdapter = true)
data class CdekPackages(
    val number: String,
    val weight: String,
    val length: String,
    val width: String,
    val height: String

)

@JsonClass(generateAdapter = true)
data class CdekItems(
    val name: String,
    val ware_key: String,
    val marking: String = "",
    val cost: String,
    val weight: String,
    val amount: String = "1",
    val payment: CdekPayment
)

@JsonClass(generateAdapter = true)
data class CdekPayment(
            val value: Int = 0
)

@JsonClass(generateAdapter = true)
data class CdekPhone(
    val number: String
)

@JsonClass(generateAdapter = true)
data class CdekLocation(
    val address: String
)
