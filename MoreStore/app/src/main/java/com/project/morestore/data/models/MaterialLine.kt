package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MaterialLine(
    val id: Long,
    val name: String,
    var isSelected: Boolean,
    val idCategory: Int
)
