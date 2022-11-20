package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FavoriteSearch(
    val id: Long,
    val value: Filter,
    val status: Int,
    var propertyValues: List<String>?
)
