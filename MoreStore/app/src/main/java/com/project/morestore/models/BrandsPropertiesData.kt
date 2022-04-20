package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BrandsPropertiesData(
    @Json(name = "id_user")
    val userId: Long?,
    val brand: List<Long>?,
    val property: List<Long>?
)
