package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)
data class PhotoData(
    val type: String,
    @Json(name = "id_type")
    val idType: Long,
    val photo: List<Photo>
)
