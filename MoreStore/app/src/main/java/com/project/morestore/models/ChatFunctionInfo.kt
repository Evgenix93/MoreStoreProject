package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatFunctionInfo(
    @Json(name = "id_dialog")
    val idDialog: Long? = null,
    @Json(name = "val")
    val value: Int? = null,
    @Json(name = "id_suggest")
    val idSuggest: Long? = null,
    val suggest: Long? = null,
    @Json(name = "id_message")
    val idMessage: Long? = null
)
