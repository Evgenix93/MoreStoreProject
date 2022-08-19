package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
//todo rename to UserAddress
data class MyAddress(
    val id :Long,
    val phone :String,
    val address :DeliveryAddress,
    @Json(name = "cdek_code")
    val cdekCode: String? = null,
    val name :String,
    @Json(name = "is_default")
    val favorite :Boolean = false,
    @Json(name = "type_save")
    val type :Int//todo use enum
) :Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class MyAddressData(
    val name :String,
    val phone :String,
    val address :DeliveryAddress,
    @Json(name = "cdek_code")
    val cdekCode: String? = null,
    @Json(name = "is_default")
    val favorite :Boolean = false,
    @Json(name = "type_save")
    val type :Int
) :Parcelable

enum class AddressType(val id :Int){ HOME(0), CDEK(1) }