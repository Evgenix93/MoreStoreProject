package com.project.morestore.repositories

import com.project.morestore.AppException
import com.project.morestore.apis.AddressesNetwork
import com.project.morestore.models.Id
import com.project.morestore.models.MyAddress
import com.project.morestore.models.MyAddressData
import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import javax.inject.Inject


class AddressesRepository @Inject constructor(private val network :AddressesNetwork){




    suspend fun getAllAddresses() :List<MyAddress>{
        return try {
            network.getAddress()
        } catch (ex :Throwable) {
            emptyList()
        }
    }

    suspend fun createAddress(newAddress :MyAddressData){
        try{
            network.createAddress(newAddress)
        } catch (ex :Exception){
            throw AppException(network.createAddressError(newAddress))
        }
    }

    suspend fun editAddress(address :MyAddress){
        try{
            network.editAddress(address)
        } catch (ex :Exception){
            throw AppException(network.editAddressError(address))
        }
    }

    suspend fun deleteAddress(address :MyAddress){
        val id = Id(address.id)
        try{
            network.deleteAddress(id)
        } catch (ex :Exception){
            throw AppException(network.deleteAddressError(id))
        }
    }
}