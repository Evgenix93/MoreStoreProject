package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BrandsPropertiesData(
    val ip: String,
    val brand: List<Long>?,
    val property: List<Long>?
)
