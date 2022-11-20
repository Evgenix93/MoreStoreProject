package com.project.morestore.apis

import com.project.morestore.data.models.CdekAddress
import retrofit2.http.GET

interface CdekAddressApi {

    //cdek/addresses
    @GET("cdek/addresses")
    suspend fun getCdekAddresses(): Array<CdekAddress>
}