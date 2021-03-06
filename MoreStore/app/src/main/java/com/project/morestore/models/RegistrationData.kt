package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationData(
    val step: Int,
    val phone: String?,
    val email: String?,
    val type: Int,
    val user: Int?,
    val code: String?,
    val name: String?,
    val surname: String?
)
