package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Feedback(
    @Json(name = "id_product")
    val id :Long,
    val rate :Byte,
    val text :String,
    val status: Byte
){
    companion object {
        const val FEEDBACK_ACTIVE_STATUS = 1
        const val FEEDBACK_ON_MODERATION_STATUS = 0
    }
}