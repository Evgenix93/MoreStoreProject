package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UploadedPhoto(
    val id: Long,
    val photo: String,
    val type: Int,
    @Json(name = "id_type")
    val idType: Int
)
