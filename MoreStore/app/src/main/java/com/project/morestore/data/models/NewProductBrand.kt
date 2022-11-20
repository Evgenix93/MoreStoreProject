package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewProductBrand(
    val id: Long?,
    var name: String?
)
