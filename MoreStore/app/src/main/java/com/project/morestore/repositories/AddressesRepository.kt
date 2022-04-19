package com.project.morestore.repositories

import com.project.morestore.AppException
import com.project.morestore.apis.AddressesNetwork
import com.project.morestore.models.MyAddress
import com.project.morestore.models.MyAddressData
import com.squareup.moshi.JsonDataException

//todo add DI
object AddressesRepository{
    private lateinit var network :AddressesNetwork

    fun init(network: AddressesNetwork){
        if(this::network.isInitialized) return
        this.network = network
    }

    suspend fun getAllAddresses() :Array<MyAddress>{
        return try {
            network.getAddress()
        } catch (ex :JsonDataException) {
            arrayOf()
        }
    }

    suspend fun createAddress(newAddress :MyAddressData){
        try{
            network.createAddress(newAddress)
        } catch (ex :Exception){
            throw AppException(network.createAddressError(newAddress))
        }
    }
}