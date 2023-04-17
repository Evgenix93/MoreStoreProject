package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class ProductStatistic(
    val wishlist: ProductWishList,
    val share: ProductShare
    ): Parcelable
