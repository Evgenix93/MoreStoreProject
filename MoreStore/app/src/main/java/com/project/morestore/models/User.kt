package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val id: Int,
    val name: String?,
    val phone: String?,
    val email: String?,
    val surname: String?,



)
