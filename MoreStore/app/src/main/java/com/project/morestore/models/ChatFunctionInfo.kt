package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ChatFunctionInfo(
    @Json(name = "id_dialog")
    val dialogId: Long,
    val suggest: Long? = null,
    @Json(name = "id_suggest")
    val suggestId: Long? = null,
    @Json(name = "val")
    val value: Int? = null

): Parcelable
