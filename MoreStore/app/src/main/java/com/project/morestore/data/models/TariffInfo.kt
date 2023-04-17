package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class TariffInfo(
    @Json(name = "id_tariff")
    val idTariff: Long,
    @Json(name = "date_start")
    val dateStart: Long,
    @Json(name = "date_end")
    val dateEnd: Long
): Parcelable
