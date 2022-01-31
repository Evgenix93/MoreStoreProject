package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductBrand(
    val id: Long,
    val name: String,
    @Json(name = "id_category")
    val idCategory: Long,
    var isChecked: Boolean?

)
