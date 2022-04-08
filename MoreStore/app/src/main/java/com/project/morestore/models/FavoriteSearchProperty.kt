package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteSearchProperty(
    val id: Long,
    @Json(name = "id_value")
    val idValue: Long
)
