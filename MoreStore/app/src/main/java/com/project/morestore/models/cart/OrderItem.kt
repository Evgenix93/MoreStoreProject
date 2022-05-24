package com.project.morestore.models.cart

import android.graphics.Bitmap
import android.os.Parcelable
import com.project.morestore.models.ChatFunctionInfo
import com.project.morestore.models.OfferedOrderPlace
import com.project.morestore.models.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val id: Long,

    //val userIcon: String,
    //val userName: String,
    val user: User?,

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
    val newAddressId: Long?,
    val chatFunctionInfo: ChatFunctionInfo?,
    val offeredOrderPlace: OfferedOrderPlace?
): Parcelable