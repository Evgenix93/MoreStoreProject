package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Suggestion(
    val text: String,
    val brand: Any,
    val category: Any,
    val product: Boolean
)
