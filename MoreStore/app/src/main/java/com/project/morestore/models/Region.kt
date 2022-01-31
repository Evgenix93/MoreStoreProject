package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Region(
    val name: String,
    var isChecked: Boolean
)