package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ProductPhoto(
    val id: Long,
    val photo: String,
    val type: Int,
    @Json(name = "id_type")
    val idType: Int
): Parcelable
