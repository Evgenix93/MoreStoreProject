package com.project.morestore.models.cart

import android.graphics.Bitmap

data class OrderItem(
    val id: Long,

    val userIcon: String,
    val userName: String,

    val photo: String,
    val name: String,

    val price: Int,
    val deliveryDate: String,
    val deliveryInfo: String,

    val status: OrderStatus,

    val newAddress: String?,
    val newTime: String?,
    val sellerId: Long,
    val productId: Long,
    val newAddressId: Long?
)