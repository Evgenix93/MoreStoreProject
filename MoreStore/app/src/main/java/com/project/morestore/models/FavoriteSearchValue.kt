package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteSearchValue(
    val id: Long? = null,
    @Json(name = "val")
    val value: Filter,
    val status: Int? = null
)
