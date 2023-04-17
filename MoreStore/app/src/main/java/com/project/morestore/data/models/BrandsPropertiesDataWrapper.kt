package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)
data class BrandsPropertiesDataWrapper(
    val id: Long,
    val data: BrandsPropertiesDataString,
    @Json(name = "id_user")
    val idUser: Long
)
