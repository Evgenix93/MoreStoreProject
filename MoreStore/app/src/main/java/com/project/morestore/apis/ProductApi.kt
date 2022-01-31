package com.project.morestore.apis

import com.project.morestore.models.ProductCategoryKids1
import com.project.morestore.models.ProductCategoryAdults
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {

    @GET("product")
    suspend fun getProducts(@Query("optionally") options: String): Response<Unit>

    @GET("category/subs?id_category=2")
    suspend fun getProductCategoriesAdults(): Response<List<ProductCategoryAdults>>?

    @GET("category/subs?id_category=1")
    suspend fun getProductCategoriesKids(): Response<List<ProductCategoryKids1>>?
}