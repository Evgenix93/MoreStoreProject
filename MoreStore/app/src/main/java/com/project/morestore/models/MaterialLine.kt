package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MaterialLine(
    val name: String,
    var isSelected: Boolean
)
