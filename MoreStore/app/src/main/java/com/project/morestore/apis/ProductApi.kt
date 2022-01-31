package com.project.morestore.apis

import com.project.morestore.models.Product
import com.project.morestore.models.ProductBrand
import com.project.morestore.models.Region
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {

    @GET("product")
    suspend fun getProducts(@Query("optionally") options: String, @Query("filter") filter: String): Response<List<Product>>

    @GET("product")
    suspend fun getProductsGetError(@Query("optionally") options: String, @Query("filter") filter: String): Response<Unit>

    @GET("product/youmaylike")
    suspend fun getYouMayLikeProducts(@Query("limit") limit: Int, @Query("id_user") userId: Int): Response<List<Product>>

    @GET("product/youmaylike")
    suspend fun getYouMayLikeProductsGetError(@Query("limit") limit: Int, @Query("id_user") userId: Int): Response<String>

    @GET("search/suggestions")
    suspend fun getSearchSuggestions(@Query("text") text: String): Response<List<String>>

    @GET("search/suggestions")
    suspend fun getSearchSuggestionsGetError(@Query("text") text: String): Response<String>

    @GET("geo/city")
    suspend fun getCities(): Response<List<Region>>

    @GET("geo/city")
    suspend fun getCitiesGetError(): Response<String>

    @GET("brand")
    suspend fun getAllBrands(): Response<List<ProductBrand>>

    @GET("brand")
    suspend fun getAllBrandsGetError(): Response<String>


















}