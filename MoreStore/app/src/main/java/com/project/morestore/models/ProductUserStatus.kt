package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ProductUserStatus(
    val buy: MessageActionSuggest?,
    val sale: MessageActionSuggest?,
    val price: MessageActionSuggest?,
    val read: Boolean
): Parcelable
