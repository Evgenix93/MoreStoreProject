package com.project.morestore.models.cart

import android.graphics.Bitmap

data class OrderItem(
    val id: Long,

    val userIcon: Bitmap,
    val userName: String,

    val photo: Bitmap,
    val name: String,

    val price: Int,
    val deliveryDate: String,
    val deliveryInfo: String,

    val status: OrderStatus,

    val newAddress: String?,
    val newTime: String?
)