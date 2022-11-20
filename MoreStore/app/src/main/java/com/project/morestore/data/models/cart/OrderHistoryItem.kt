package com.project.morestore.data.models.cart

import com.project.morestore.data.models.ChatFunctionInfo
import com.project.morestore.data.models.OfferedOrderPlace
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.User

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