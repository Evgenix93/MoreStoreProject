package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductCategory3(
    val id: Int,
    val name: String,
    var isChecked: Boolean?
)
