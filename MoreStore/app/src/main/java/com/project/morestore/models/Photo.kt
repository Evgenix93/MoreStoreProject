package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)
data class Photo(
    val type: String,
    val base64: String
)
