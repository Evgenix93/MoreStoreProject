package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatingMessage(
    val id_dialog: Long,
    val text: String

)
