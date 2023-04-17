package com.project.morestore.data.apis

import com.project.morestore.data.models.Region
import retrofit2.http.GET

interface CitiesApi {
    @GET("geo/city")
    suspend fun getCities(): List<Region>
}