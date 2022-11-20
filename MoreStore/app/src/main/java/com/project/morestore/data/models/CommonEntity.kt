package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonEntity(
    val id: Int,
    val title: String,
    val property: List<PropertyType>?
)
