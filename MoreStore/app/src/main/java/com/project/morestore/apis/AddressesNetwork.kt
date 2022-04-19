package com.project.morestore.apis

import com.project.morestore.models.Id
import com.project.morestore.models.MyAddress
import com.project.morestore.models.MyAddressData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AddressesNetwork {

    @GET("save_address")
    suspend fun getAddress() :Array<MyAddress>

    @POST("save_address")
    suspend fun createAddress(
        @Body newAddress :MyAddressData
    ) :Id

    @POST("save_address")
    suspend fun createAddressError(
        @Body newAddress :MyAddressData
    ) :String
}