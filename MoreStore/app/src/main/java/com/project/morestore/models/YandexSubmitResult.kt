package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YandexSubmitResult(
    val code: Int?
)

@JsonClass(generateAdapter = true)
data class YandexCancelResult(
    val code: Int?,
    val status: String?
)
