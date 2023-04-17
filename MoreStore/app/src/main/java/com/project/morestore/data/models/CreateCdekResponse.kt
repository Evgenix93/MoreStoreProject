package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateCdekResponse(
    val entity: CreateCdekResponseEntity

)

@JsonClass(generateAdapter = true)
data class CreateCdekResponseEntity(
    val uuid: String
)
