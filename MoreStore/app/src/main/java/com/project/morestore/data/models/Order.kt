package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Order(
    val id: Long,
    val delivery: Int,
    val pay: Int,
    @Json(name = "id_cart")
    val idCart: List<Long>,
    val cart: List<Product>?,
    val status: Int,
    @Json(name = "place_address")
    val placeAddress: String?,
    val place: Int,
    @Json(name = "id_user")
    val idUser: Long?,
    @Json(name = "id_seller")
    val idSeller: Long?,
    val sum: Float,
    @Json(name = "is_payment")
    val isPayment: Boolean,
    @Json(name = "id_cdek")
    val idCdek: String?,
    @Json(name = "id_yandex")
    val idYandex: String?,
    var deliveryStatus: String? = null,
    val comment: String?,
    val sberId: String?,
    val wallet: OrderWallet?
): Parcelable
