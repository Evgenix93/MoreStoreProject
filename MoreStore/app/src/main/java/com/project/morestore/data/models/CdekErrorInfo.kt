package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CdekErrorInfo(
    val errors: List<CdekError>
)

@JsonClass(generateAdapter = true)
data class CdekError(
    val code: String,
    val message: String
)
