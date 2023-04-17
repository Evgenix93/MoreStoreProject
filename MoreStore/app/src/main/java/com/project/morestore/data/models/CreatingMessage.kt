package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatingMessage(
    val id_dialog: Long,
    val text: String

)
