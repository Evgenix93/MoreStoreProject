package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ProductBrand(
    val id: Long,
    val name: String,
    @Json(name = "id_category")
    val idCategory: Long?,
    var isChecked: Boolean?,
    var isWished: Boolean?,
    val status: Int?

): Parcelable
