package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CdekOrder(
    val id_order: Long,
    val shipment_point1: String,
    val delivery_point1: String,
    val sender: CdekSender,
    val recipient: CdekRecipient,
    val packages: CdekPackages

)

@JsonClass(generateAdapter = true)
data class CdekSender(
    val company: String,
    val name: String,
    val email: String,
    val phones: List<String>,
    val number: String
)

@JsonClass(generateAdapter = true)
data class CdekRecipient(
    val name: String,
    val phones: List<String>,
    val number: String
)

@JsonClass(generateAdapter = true)
data class CdekPackages(
    val number: String,
    val weight: String,
    val length: String,
    val width: String,
    val height: String

)
