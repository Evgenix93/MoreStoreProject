package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DialogWrapper(
    val product: Product?,
    val dialog: Dialog,
    val messages: List<MessageModel>?
    )
