package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatingDialog(
    val id_user: Long,
    val id_product: Long
)
