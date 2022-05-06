package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass (generateAdapter = true)
@Parcelize
data class PhotoVideo(
    val type: String,
    val base64: String
): Parcelable
