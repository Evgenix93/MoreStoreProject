package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Region(
    val id: Long,
    val name: String,
    @Json(name = "id_country")
    val idCountry: Long,
    var isChecked: Boolean = false
)
