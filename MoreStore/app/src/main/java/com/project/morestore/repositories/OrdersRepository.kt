package com.project.morestore.repositories

import android.content.Context
import android.util.Log
import com.project.morestore.models.*
import com.project.morestore.models.cart.CartItem
import com.project.morestore.singletones.Network
import com.project.morestore.util.DeviceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class OrdersRepository(private val context: Context) {

    private val ordersApi = Network.ordersApi

    suspend fun getCartItems(userId: Long?): Response<List<CartItem>?>? {

        var ip: String? = null;
        if (userId == null) {
            ip = DeviceUtils.getIPAddress(true)
        }

        return try {
            ordersApi.sendProblem(userId, ip, "user,property")
        } catch (e: Throwable) {
            Log.e("TEST", "getCartItems: " + e)
            Response.error(400, "".toResponseBody(null))
        }
    }

    suspend fun addCartItem(productId: Long, userId: Long?): Response<Boolean>? {

        var ip: String? = null;
        if (userId == null) {
            ip = DeviceUtils.getIPAddress(true)
        }

        val body = AddCartData(productId, userId, ip)
        return try {
            ordersApi.addToCart(body)
        } catch (e: Exception) {
            Response.error(
                400, e.message?.toResponseBody(null)
                    ?: "сетевая ошибка".toResponseBody(null)
            )
        }
    }

    suspend fun removeCartItem(productId: Long, userId: Long?): Response<Boolean>? {

        var ip: String? = null;
        if (userId == null) {
            ip = DeviceUtils.getIPAddress(true)
        }

        val body = AddCartData(productId, userId, ip)
        return try {
            ordersApi.removeFromCart(body)
        } catch (e: Exception) {
            Response.error(
                400, e.message?.toResponseBody(null)
                    ?: "сетевая ошибка".toResponseBody(null)
            )
        }
    }

    suspend fun orderProblem(productProblem: ProductProblemsData) {
        try {
            ordersApi.sendProblem(productProblem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun createOrder(order: NewOrder): Response<Unit>?{
        return try {
            ordersApi.createOrder(order)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.createOrderGetError(order)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: e.message.toString().toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, e.message.toString().toResponseBody(null))
                }
            }
        }
    }

    suspend fun getAllOrders(): Response<List<Order>>?{
        return try {
            ordersApi.getAllOrders()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.getAllOrdersGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: e.message.toString().toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, e.message.toString().toResponseBody(null))
                }
            }
        }
    }

    suspend fun getOrderAddresses(): Response<List<OfferedOrderPlace>>?{
        return try {
            ordersApi.getOrderAddresses()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.getOrderAddressesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: e.message.toString().toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, e.message.toString().toResponseBody(null))
                }
            }
        }
    }

    suspend fun changeOrderPlaceStatus(change: OfferedOrderPlaceChange): Response<Unit>?{
        return try {
            ordersApi.changeOrderPlaceStatus(change)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.changeOrderPlaceStatusGetError(change)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: e.message.toString().toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, e.message.toString().toResponseBody(null))
                }
            }
        }
    }

    suspend fun submitReceiveOrder(orderId: OrderId): Response<Unit>?{
        return try {
            ordersApi.submitReceiveOrder(orderId)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = ordersApi.submitReceiveOrderGetError(orderId)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: e.message.toString().toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, e.message.toString().toResponseBody(null))
                }
            }
        }
    }

}