package com.project.morestore.apis

import com.project.morestore.models.DealPlace
import com.project.morestore.models.Order
import com.project.morestore.models.cart.OrderItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SalesApi {

    @GET("order/my")
    suspend fun getSales(): Response<List<Order>>

    @GET("order/my")
    suspend fun getSalesErrorString(): String

    @POST("order/address")
    suspend fun addDealPlace(@Body dealPlace: DealPlace): Response<Boolean>

    @GET("order/address")
    suspend fun getAddresses(): Response<List<DealPlace>>
}