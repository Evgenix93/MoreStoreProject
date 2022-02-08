package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Property(
    val id: Long,
    val name: String,
    val ico: String?,
    @Json(name = "id_category")
    val idCategory: Long
)
