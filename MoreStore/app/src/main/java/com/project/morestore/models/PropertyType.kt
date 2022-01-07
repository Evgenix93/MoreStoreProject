package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PropertyType(
    val id: Int,
    val name: String,
    val type: String,
    @Json(name = "is_filter")
    val isFilter: Int,
    @Json(name = "id_category")
    val idCategory: Int,
    val property: List<Size>?
)
