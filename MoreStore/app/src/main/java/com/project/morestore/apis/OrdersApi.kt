package com.project.morestore.apis

import com.project.morestore.models.AddCartData
import com.project.morestore.models.CreateProductData
import com.project.morestore.models.CreatedProductId
import com.project.morestore.models.ProductProblemsData
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

}