package com.project.morestore.models

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
    val code: String
)

object CdekStatuses {
    const val INVALID = "INVALID"
    const val ACCEPTED = "ACCEPTED"
    const val CREATED = "CREATED"

}
