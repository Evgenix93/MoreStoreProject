package com.project.morestore.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class CdekOrderInfo(
    //val requests: List<CdekOrderInfoRequestStatus>
    val entity: CdekOrderInfoBody

): Parcelable

@JsonClass(generateAdapter = true)
data class CdekOrderInfoRequestStatus(
    val state: String
)

@Parcelize
@JsonClass(generateAdapter = true)
data class CdekOrderInfoBody(
    @Json(name = "cdek_number")
    val cdekNumber: String,
    val statuses: List<CdekStatus>,
    val uuid: String,
    val number: String?,
    val tariff_code: Int?,
    val delivery_point: String?,
    val sender: Participant?,
    val seller: Seller?,
    val recipient: Participant?,
    val from_location: Location?,
    val to_location: Location?,
    val delivery_detail: DeliveryDetail?

    //val requests: List<CdekOrderInfoRequestStatus>
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CdekStatus(
    val code: String,
    val name: String
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Participant(
    val company: String,
    val name: String,
    val phones: List<PhoneNumber>,
    ): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class PhoneNumber(
    val number: String
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Seller(
    val name: String
): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Location(
    val code: Int?,
    val city: String?,
    val country_code: String?,
    val country: String?,
    val region: String?,
    val region_code: Int?,
    val address: String?,
    val postal_code: String?

): Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class DeliveryDetail(
    val delivery_sum: Float?,
    val total_sum: Float
): Parcelable

object CdekStatuses {
    const val INVALID = "INVALID"
    const val ACCEPTED = "ACCEPTED"
    const val CREATED = "CREATED"
    const val READY_TO_SHIP = "READY_FOR_SHIPMENT_IN_SENDER_CITY"
    const val TAKEN_BY_TRANSPORTER_FROM_SENDER_CITY = "TAKEN_BY_TRANSPORTER_FROM_SENDER_CITY"
    const val SENT_TO_TRANSIT_CITY = "SENT_TO_TRANSIT_CITY"
    const val ACCEPTED_IN_TRANSIT_CITY = "ACCEPTED_IN_TRANSIT_CITY"
    const val ACCEPTED_AT_TRANSIT_WAREHOUSE = "ACCEPTED_AT_TRANSIT_WAREHOUSE"


}
