package com.project.morestore.apis

import com.project.morestore.models.NewBrand
import com.project.morestore.models.NewProductBrand
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BrandApi {
    @POST("brand")
    suspend fun addBrand(@Body newBrand: NewBrand): Response<String>
}