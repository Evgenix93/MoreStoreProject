package com.project.morestore.data.apis

import com.project.morestore.data.models.Category
import com.project.morestore.data.models.Size
import retrofit2.Response
import retrofit2.http.GET

interface OnboardingApi {

    @GET("brand/category")
    suspend fun getCategories(): Response<List<Category>>

    @GET("brand/category")
    suspend fun getCategoriesGetError(): Response<String>

    @GET("property")
    suspend fun getAllSizes(): Response<List<Size>>

    @GET("property")
    suspend fun getAllSizesGetError(): Response<String>


}