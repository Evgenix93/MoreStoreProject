package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Product(
    val id: Long,
    val name: String,
    val about: String,
    val phone: String,
    @Json(name = "phone_show")
    val phoneShow: String,
    val status: Int,
    val price: Float,
    val sale: Float,
    val date: Long,
    val address: Address,
    val photo: List<ProductPhoto>,
    val user: User,
    //val category: Category,
    val statistic: ProductStatistic,
    val brand: ProductBrand,
    val property: List<Property>

): Parcelable
