package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@JsonClass(generateAdapter = true)
@Parcelize
data class Product(
    val id: Long,
    val name: String,
    val about: String,
    val phone: String?,
    @Json(name = "phone_show")
    val phoneShow: String?,
    var status: Int,
    @Json(name = "comment_moderation")
    val commentModeration: String?,
    val price: Float,
    @Json(name = "price_new")
    val priceNew: Float?,
    val sale: Float,
    val date: Long,
    val address: Address?,
    val photo: List<ProductPhoto>,
    val video: List<ProductVideo>?,
    val user: User?,
    val category: Category?,
    val statistic: ProductStatistic?,
    val brand: ProductBrand?,
    val property: List<Property>?,
    @Json(name = "id_user")
    val idUser: Long?,
    val wishlist: Boolean?,
    @Json(name = "status_user")
    val statusUser: ProductUserStatus?,
    @Json(name = "package")
    val packageDimensions: ProductDimensions,
    val tariff: TariffInfo?
): Parcelable
