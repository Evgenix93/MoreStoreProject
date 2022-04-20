package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BrandsPropertiesDataString(
    val brand: String?,
    val property: String?
)
