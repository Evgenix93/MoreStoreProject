package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)
data class PhotoVideo(
    val type: String,
    val base64: String
)
