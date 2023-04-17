package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Address(
    @Json(name = "full_address")
    val fullAddress: String?,
    val city: Int?,
   // @Json(name = "full_city")
    //val fullCity: ProductCity
    val coords: Coords? = null

): Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class Coords(
    val lat: Double,
    val lon: Double
): Parcelable
