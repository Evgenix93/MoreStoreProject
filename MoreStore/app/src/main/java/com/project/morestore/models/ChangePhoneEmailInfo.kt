package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangePhoneEmailInfo(
    val err: String
)
