package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class MessageActionSuggest(
    val id: Long,
    val status: Int,
    @Json(name = "val")
    val value: String?,
    @Json(name = "id_canceled")
    val idCanceled: Long?,
    @Json(name = "id_user")
    val idUser: Long?

): Parcelable
