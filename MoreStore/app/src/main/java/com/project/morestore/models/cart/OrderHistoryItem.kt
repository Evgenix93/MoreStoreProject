package com.project.morestore.models.cart

import android.graphics.Bitmap

data class OrderHistoryItem(
    val id: String,

    val userIcon: String,
    val userName: String,

    val photo: String,
    val name: String,

    val price: Int,
    val deliveryDate: String,
    val deliveryInfo: String,

    val status: OrderHistoryStatus
)