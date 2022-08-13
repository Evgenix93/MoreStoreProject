package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Card(
    val id: Long?,
    val number: String,
    val active: Int
)
