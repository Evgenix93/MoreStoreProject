package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CdekOrder(
    val id_order: Long,
    val shipment_point1: String,
    val delivery_point1: String,
    val sender: CdekSender,
    val recipient: CdekRecipient,
    val packages: CdekPackages,
    val items: CdekItems

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
