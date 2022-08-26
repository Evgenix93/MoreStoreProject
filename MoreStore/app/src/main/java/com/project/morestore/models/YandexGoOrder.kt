package com.project.morestore.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YandexGoOrder(
    @Json(name = "id_order")
    val idOrder: Long,
    val comment: String,
    @Json(name = "emergency_contact_name")
    val emergencyContactName: String,
    @Json(name = "emergency_contact_phone")
    val emergencyContactPhone: String,
    @Json(name = "items_quantity")
    val itemQuantity: String,
   @Json(name = "items_product_name")
    val itemsProductName: String,
   @Json(name = "product_price")
    val productPrice: String,
   @Json(name = "route_points_address_coordinates")
    val pointAddressCoordinates: String,
   @Json(name = "route_points_address_fullname")
    val pointFullName: String,
   @Json(name = "route_points_contact_email")
    val pointContactEmail: String,
   @Json(name = "route_points_contact_name")
    val pointContactName: String,
   @Json(name = "route_points_contact_phone")
    val pointContactPhone: String,
    @Json(name = "take_route_points_address_coordinates")
    val takePointCoordinates: String,
    @Json(name = "take_route_points_address_fullname")
    val takePointFullName: String,
    @Json(name = "take_route_points_contact_email")
    val takePointContactEmail: String,
    @Json(name = "take_route_points_contact_name")
    val takePointContactName: String,
    @Json(name = "take_route_points_contact_phone")
    val takePointContactPhone: String
)

@JsonClass(generateAdapter = true)
data class YandexClaimId(
    @Json(name = "claim_id")
    val claimId: Long
)
