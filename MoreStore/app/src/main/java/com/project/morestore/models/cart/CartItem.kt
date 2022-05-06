package com.project.morestore.models.cart

import android.os.Parcelable
import com.project.morestore.models.Product
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class CartItem(
    val id: Long,
    val product: Product
) : Parcelable