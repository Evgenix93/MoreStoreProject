package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OfferedOrderPlace(
    val id: Long,
    @Json(name = "id_user")
    val idUser: Long,
    @Json(name = "id_seller")
val idSeller: Long,
    @Json(name = "id_order")
val idOrder: Long,
val address: String,
val status: Int,
val type: String

)

enum class OfferedPlaceType(val value: String){
    APPLICATION("application"),
    PROPOSED("proposed")
}
