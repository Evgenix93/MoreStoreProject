package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Feedback(
    @Json(name = "id_product")
    val id :Long,
    val rate :Byte,
    val text :String
)