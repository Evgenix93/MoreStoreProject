package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Banner(
    val id: Long,
    val title: String,
    val text: String,
    val color: BannerTextColor,
    val url: String?,
    val sex: String,
    val photo: ProductPhoto?
)

@JsonClass(generateAdapter = true)
data class BannerTextColor(
            val title: String,
            val text: String
        )
