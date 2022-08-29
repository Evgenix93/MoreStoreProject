package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationData(
    val step: Int? = null,
    val phone: String? = null,
    val email: String? = null,
    val type: Int? = null,
    val user: Int? = null,
    val code: String? = null,
    val name: String? = null,
    val surname: String? = null
)
