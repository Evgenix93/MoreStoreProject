package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Property(
    val id: Long,
    val name: String,
    val ico: String?,
    @Json(name = "id_category")
    val idCategory: Long?,
    var isChecked: Boolean?,
    @Json(name = "id_property")
    val idProperty: Long? = null,
    val value: String? = null,
    val comment: String? = null

): Parcelable
