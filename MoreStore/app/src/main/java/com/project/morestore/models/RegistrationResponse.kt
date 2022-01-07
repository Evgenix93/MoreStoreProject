package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationResponse(
    val user: String?,
    val step: Int?,
    val code: Int?,
    val token: String?,
    val email: ChangePhoneEmailInfo?,
    val phone: ChangePhoneEmailInfo?
)
