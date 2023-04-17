package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BrandsPropertiesDataString(
    @Json(name = "id_user")
    val userId: Long?,
    val brand: String?,
    val property: String?
    )
