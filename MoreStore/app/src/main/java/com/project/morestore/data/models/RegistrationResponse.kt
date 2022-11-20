package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationResponse(
    val user: String?,
    val step: Int?,
    val code: Int?,
    val token: String?,
    val email: ChangePhoneEmailInfo?,
    val phone: ChangePhoneEmailInfo?,
    val RES: TokenData?,
    val expires: Long?
)
