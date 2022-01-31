package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductCategoryKids1(
    val id: Int,
    val name: String,
    val sub: List<ProductCategory>,
    var isChecked: Boolean?
)
