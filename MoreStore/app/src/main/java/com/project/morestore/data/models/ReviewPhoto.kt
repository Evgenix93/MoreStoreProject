package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ReviewPhoto(
    val id :Long,
    val photo :String,
)
