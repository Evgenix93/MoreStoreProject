package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CdekCalculatePriceInfo(
    val tariff_code: Int,
    val from_location: AddressString ,
    val to_location: AddressString,
    val packages: ProductDimensions
    )
@JsonClass(generateAdapter = true)
data class AddressString(val address: String)

