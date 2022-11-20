package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductCategoryKids3(
    val id: Int,
    val name: String,
    var isChecked: Boolean?
)
