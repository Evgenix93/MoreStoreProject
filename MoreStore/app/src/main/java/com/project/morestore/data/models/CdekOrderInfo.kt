package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CdekOrderInfo(
    //val requests: List<CdekOrderInfoRequestStatus>
    val entity: CdekOrderInfoBody

)

@JsonClass(generateAdapter = true)
data class CdekOrderInfoRequestStatus(
    val state: String
)

@JsonClass(generateAdapter = true)
data class CdekOrderInfoBody(
    val statuses: List<CdekStatus>
    //val requests: List<CdekOrderInfoRequestStatus>
)

@JsonClass(generateAdapter = true)
data class CdekStatus(
    val code: String,
    val name: String
)

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
