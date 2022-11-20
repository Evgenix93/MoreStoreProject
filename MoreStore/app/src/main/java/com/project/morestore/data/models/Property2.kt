package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Property2(
    val value: Long,
    @Json(name = "property_category")
    val propertyCategory: Long
)
