package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CardActiveData(
    val active: Int
)
