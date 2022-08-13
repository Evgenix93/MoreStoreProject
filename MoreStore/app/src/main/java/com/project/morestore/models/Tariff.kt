package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tariff (
    val type: String,
    @Json(name = "id_product")
    val idProduct: Long,
    @Json(name = "id_tariff")
    val idTariff: Long
        )