package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewOrder(
    val cart: List<Long>,
    val delivery: Int,
    val place: OrderPlace,
    val pay: Int,
    val comment: String? = null,
    val promocode: String? = null

)
