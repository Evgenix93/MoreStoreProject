package com.project.morestore.apis

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {

    @GET("product")
    suspend fun getProducts(@Query("optionally") options: String): Response<List<Product>>
}