package com.project.morestore.apis

import com.project.morestore.data.models.Address
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApi {

    @GET("geo/geocoder")
    suspend fun decodeCity(@Query("coords") coords: String): Response<Address>

    @GET("geo/geocoder")
    suspend fun getCoordsForAddress(@Query("address") address: String): Response<Address>


}