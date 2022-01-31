package com.project.morestore.apis

import com.project.morestore.models.Region
import retrofit2.http.GET

interface FilterApi {

    @GET("geo/city")
    suspend fun getCities(): List<Region>
}