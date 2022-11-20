package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
 data class ProductCategory(
    val id: Int,
    val name: String,
    var isChecked: Boolean?
 ): Parcelable
