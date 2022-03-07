package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Suggestion(
    val text: String,
    val brand: Long?,
    val category: Long?,
    val product: Boolean?
)
