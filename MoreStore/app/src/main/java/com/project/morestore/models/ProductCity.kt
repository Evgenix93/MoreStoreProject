package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ProductCity(
    val id: Long,
    val name: String
    ): Parcelable
