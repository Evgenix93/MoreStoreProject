package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewBrand(
    val name :String,
    @Json(name = "id_category")
    val category :Int = 3,

)
/*
{
    "name": "...",
    "id_category": 3
}
 */