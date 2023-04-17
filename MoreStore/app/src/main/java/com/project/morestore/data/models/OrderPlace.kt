package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderPlace(
    val id: Long,
    val address: String?,
val date: Long?
)
