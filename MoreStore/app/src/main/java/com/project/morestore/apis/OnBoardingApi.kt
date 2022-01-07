package com.project.morestore.apis

import com.project.morestore.models.CommonEntity
import com.project.morestore.models.Size
import retrofit2.Response
import retrofit2.http.GET

interface OnBoardingApi {

    @GET("onboard")
    suspend fun getOnBoardingInfo(): Response<List<CommonEntity?>>

    @GET("property")
    suspend fun getAllSizes(): Response<List<Size>>
}