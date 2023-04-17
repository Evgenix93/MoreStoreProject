package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ProductDimensions(
    val length: String?,
    val width: String?,
    val height: String?,
    val weight: String?
): Parcelable
