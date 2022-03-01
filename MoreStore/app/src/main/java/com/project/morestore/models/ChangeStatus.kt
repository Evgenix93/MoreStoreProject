package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass (generateAdapter = true)
data class ChangeStatus(
    val id: Long,
    val status: Int
)
