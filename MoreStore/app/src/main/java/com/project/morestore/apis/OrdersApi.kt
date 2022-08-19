package com.project.morestore.apis

import com.project.morestore.models.*
import com.project.morestore.models.cart.CartItem
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*


interface OrdersApi {

    @GET("product/cart")
    suspend fun sendProblem(@Query("id_user") userId: Long?, @Query("ip") ip: String?, @Query("optionally") options: String): Response<List<CartItem>?>

    @POST("product/cart")
    suspend fun addToCart(@Body productData: AddCartData): Response<Boolean>

    @HTTP(method = "DELETE", path = "product/cart", hasBody = true)
    suspend fun removeFromCart(@Body productData: AddCartData): Response<Boolean>

    @POST("/product/complaint")
    suspend fun sendProblem(@Body problem: ProductProblemsData): Response<Boolean>

    @POST("order")
    suspend fun createOrder(@Body order: NewOrder): Response<Unit>

    @POST("order")
    suspend fun createOrderGetError(@Body order: NewOrder): Response<String>

    @GET("order")
    suspend fun getAllOrders(): Response<List<Order>>

    @GET("order")
    suspend fun getAllOrdersGetError(): Response<String>

    @GET("order/address")
    suspend fun getOrderAddresses(): Response<List<OfferedOrderPlace>>

    @GET("order/address")
    suspend fun getOrderAddressesGetError(): Response<String>

    @POST("order/put_address")
    suspend fun changeOrderPlaceStatus(@Body addressChange: OfferedOrderPlaceChange): Response<Unit>

    @POST("order/put_address")
    suspend fun changeOrderPlaceStatusGetError(@Body addressChange: OfferedOrderPlaceChange): Response<String>

    @POST("order/successfully")
    suspend fun submitReceiveOrder(@Body orderId: OrderId): Response<Unit>

    @POST("order/successfully")
    suspend fun submitReceiveOrderGetError(@Body orderId: OrderId): Response<String>

    @GET("order/promo")
    suspend fun getPromoCodeInfo(@Query("code") code: String): Response<PromoCode>

    @POST("sber/registration")
    suspend fun payOrder(@Body payInfo: PayOrderInfo): Response<PaymentUrl>

    @POST("sber/registration")
    suspend fun payOrderGetError(@Body payInfo: PayOrderInfo): Response<String>

    @POST("cdek/price")
    suspend fun getCdekPrice(@Body info: CdekCalculatePriceInfo): Response<DeliveryPrice>

    @POST("cdek/price")
    suspend fun getCdekPriceError(@Body info: CdekCalculatePriceInfo): Response<String>

    @POST("cdek/add_new")
    suspend fun createCdekOrder(@Body order: CdekOrder): Response<Unit>

    @POST("cdek/add_new")
    suspend fun createCdekOrderGetError(@Body order: CdekOrder): Response<String>

}