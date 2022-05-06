package com.project.morestore.apis

import com.project.morestore.models.CdekAddress
import retrofit2.http.GET

interface CdekAddressApi {

    //cdek/addresses
    @GET("cdek/addresses")
    suspend fun getCdekAddresses(): Array<CdekAddress>
}