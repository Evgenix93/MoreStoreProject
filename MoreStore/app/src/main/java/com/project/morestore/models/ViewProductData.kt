package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ViewProductData(
    @Json(name = "id_user")
    val idUser: Long,
    @Json(name = "id_product")
    val idProduct: Long
)
