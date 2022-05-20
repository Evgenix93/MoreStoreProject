package com.project.morestore.models.cart

import android.graphics.Bitmap
import com.project.morestore.models.User

data class OrderHistoryItem(
    val id: String,

    //val userIcon: String,
    //val userName: String,
    val user: User?,

    val photo: String,
    val name: String,

    val price: Int,
    val deliveryDate: String,
    val deliveryInfo: String,

    val status: OrderHistoryStatus
)