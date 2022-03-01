package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductVideo(
    val id: Long,
    val video: String,
    val type: Int,
    @Json(name = "id_type")
    val idType: Int
)
