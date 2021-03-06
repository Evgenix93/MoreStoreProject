package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SizeLine(
    var id: Int,
    val int: String?,
    val w: String?,
    val itRuFr: String?,
    val us: String?,
    val uk: String?,
    var isSelected: Boolean,
    var idCategory: Int
)
