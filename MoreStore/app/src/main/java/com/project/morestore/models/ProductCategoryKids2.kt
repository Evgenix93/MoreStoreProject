package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductCategoryKids2(
    val id: Int,
    val name: String,
    val sub: List<ProductCategory>,
    var isChecked: Boolean?
)
