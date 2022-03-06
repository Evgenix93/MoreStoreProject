package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Dialog(
    val id: Long,
    val name: String,
    val user: User,


)
