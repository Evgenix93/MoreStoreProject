package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeliveryAddress(
    val city :String,//город
    val street :String,//улица
    @Json(name = "house_index")
    val index :String,//почтовый индекс
    val house :String,//дом
    @Json (name = "house_frame")
    val housing :String?,//корпус
    @Json(name = "house_structure")
    val building :String?,//строение
    @Json(name = "house_flat")
    val apartment :String?//квартира
)

/*
{
    "city": "Санкт-петербург",
    "street": "Кременчугская",
    "house_index": "191036",
    "house": "9",
    "house_frame": "2",
    "house_structure": null,
    "house_flat": "46"
}
 */