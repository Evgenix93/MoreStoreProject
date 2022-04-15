package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageActionSuggest(
    val id: Long,
    @Json(name = "val")
    val value: String?,
    val status: Int
)
