package com.project.morestore.models.cart

import android.graphics.Bitmap
import com.project.morestore.models.ChatFunctionInfo
import com.project.morestore.models.OfferedOrderPlace
import com.project.morestore.models.Product
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

    val status: OrderHistoryStatus,
    val product: Product,
    val newAddress: String?,
    val newTime: String?,
    val sellerId: Long,
    val productId: Long,
    val newAddressId: Long?,
    val chatFunctionInfo: ChatFunctionInfo? = null,
    val offeredOrderPlace: OfferedOrderPlace? = null
)