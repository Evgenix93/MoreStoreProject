package com.project.morestore.data.apis

import com.project.morestore.data.models.CdekAddress
import retrofit2.http.GET

interface CdekAddressApi {

    //cdek/addresses
    @GET("cdek/addresses")
    suspend fun getCdekAddresses(): List<CdekAddress>
}