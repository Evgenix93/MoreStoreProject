package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDimensions(
    val length: String,
    val width: String,
    val height: String,
    val weight: String
)
