package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
 data class ProductCategory2(
     val id: Int,
     val name: String,
     val isChecked: Boolean?
)
