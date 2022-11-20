package com.project.morestore.repositories

import android.util.Log
import com.project.morestore.apis.OrdersApi
import com.project.morestore.models.AddCartData
import com.project.morestore.models.cart.CartItem
import com.project.morestore.singletones.Network
import com.project.morestore.util.DeviceUtils
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

class CartRepository @Inject constructor(private val ordersApi: OrdersApi) {


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

}