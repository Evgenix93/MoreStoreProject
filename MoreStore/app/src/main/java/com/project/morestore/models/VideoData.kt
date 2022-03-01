package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoData(
    val type: String,
    @Json(name = "id_type")
    val idType: Long,
    val video: List<PhotoVideo>
)

