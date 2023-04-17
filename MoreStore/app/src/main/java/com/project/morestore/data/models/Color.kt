package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Color(
    val name: String,
    val color: Int,
    var isChecked: Boolean,
    val idCategory: Int
)
