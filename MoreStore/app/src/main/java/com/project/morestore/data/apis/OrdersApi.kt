package com.project.morestore.data.apis

import com.project.morestore.data.models.*
import com.project.morestore.data.models.cart.CartItem
import retrofit2.Response
import retrofit2.http.*


interface OrdersApi {

    @GET("product/cart")
    suspend fun sendProblem(@Query("id_user") userId: Long?, @Query("ip") ip: String?, @Query("optionally") options: String): Response<List<CartItem>?>

    @POST("product/cart")
    suspend fun addToCart(@Body productData: AddCartData): Response<Boolean>

    @POST("product/cart")
    suspend fun addToCartGetError(@Body productData: AddCartData): Response<String>

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

    @POST("sber/return_money")
    suspend fun returnMoney(@Body returnMoneyInfo: ReturnMoneyInfo): Response<Unit>

    @POST("sber/registration")
    suspend fun payOrderGetError(@Body payInfo: PayOrderInfo): Response<String>

    @POST("cdek/price")
    suspend fun getCdekPrice(@Body info: CdekCalculatePriceInfo): Response<DeliveryPrice>

    @POST("cdek/price")
    suspend fun getCdekPriceError(@Body info: CdekCalculatePriceInfo): Response<String>

    @POST("cdek/add_new")
    suspend fun createCdekOrder(@Body order: CdekOrder): Response<CreateCdekResponse>

    @POST("cdek/add_new")
    suspend fun createCdekOrderGetError(@Body order: CdekOrder): Response<String>

    @GET("cdek/info_order")
    suspend fun getCdekOrderInfo(@Query("id_order_cdek") id: String): Response<CdekOrderInfo>

    @POST("yandexgo/add_new")
    suspend fun createYandexGoOrder(@Body order: YandexGoOrder): Response<YandexOrderInfoBody>

    @POST("yandexgo/add_new")
    suspend fun createYandexGoOrderGetError(@Body order: YandexGoOrder): Response<String>

    @GET("yandexgo/info_order")
    suspend fun getYandexGoOrderInfo(@Query("claim_id") id: String): Response<YandexOrderInfoBody>

    @GET("yandexgo/info_order")
    suspend fun getYandexGoOrderInfoGetError(@Query("claim_id") id: String): Response<String>

    @POST("yandexgo/accept")
    suspend fun submitYandexGoOrder(@Body claimId: YandexClaimId): Response<YandexSubmitResult>

    @POST("yandexgo/accept")
    suspend fun submitYandexGoOrderGetError(@Body claimId: YandexClaimId): Response<String>

    @POST("yandexgo/price")
    suspend fun getYandexGoPrice(@Body info: YandexPriceCalculateInfo): Response<YandexPriceResult>

    @POST("yandexgo/price")
    suspend fun getYandexGoPriceGetError(@Body info: YandexPriceCalculateInfo): Response<String>

    @POST("yandexgo/cancel")
    suspend fun cancelYandexGoOrder(@Body claimId: YandexCancelClaimId): Response<YandexCancelResult>

    @POST("yandexgo/cancel")
    suspend fun cancelYandexGoOrderGetError(@Body claimId: YandexCancelClaimId): Response<String>

}