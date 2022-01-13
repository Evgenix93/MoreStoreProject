package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationResponse2(
    val email: String?,
    val phone: String?
)
