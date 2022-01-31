package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class UserAvatar(
    val photo: String
    ): Parcelable
