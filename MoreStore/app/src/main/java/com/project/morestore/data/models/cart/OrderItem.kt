package com.project.morestore.data.models.cart

import android.os.Parcelable
import com.project.morestore.data.models.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItem(
    val id: Long,

    //val userIcon: String,
    //val userName: String,
    val user: User?,

    val photo: String,
    val name: String,

    val price: Float,
    val deliveryDate: String,
    val deliveryInfo: String,

    var status: OrderStatus,

    var newAddress: String?,
    val newTime: String?,
    val sellerId: Long,
    val productId: Long,
    val newAddressId: Long?,
    val product: Product,
    val chatFunctionInfo: ChatFunctionInfo? = null,
    val offeredOrderPlace: OfferedOrderPlace? = null,
    val buyerId: Long? = null,
    val cdekYandexAddress: String? = null,
    val promo: String? = null,
    val deliveryStatusInfo: String? = null,
    val yandexGoOrderId: String? = null,
    val cdekInfoEntity: CdekOrderInfo? = null,
    val sberId: String?,
    val sum: Int
): Parcelable
