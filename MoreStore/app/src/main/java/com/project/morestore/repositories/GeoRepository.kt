package com.project.morestore.repositories

import com.project.morestore.GeoPosition
import com.project.morestore.apis.GeoApi
import com.project.morestore.models.Address
import com.project.morestore.singletones.Network
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class GeoRepository @Inject constructor(private val network: GeoApi) {


    suspend fun getCity(geoposition :GeoPosition) :Address? {
        return network.decodeCity("${geoposition.lat},${geoposition.lon}").body()
    }

    suspend fun getCoordsByAddress(address: String): Response<Address>?{
        return try {
            network.getCoordsForAddress(address)
        }catch (e: Throwable){
            if(e is IOException)
                null
            else Response.error(400, "ошибка".toResponseBody(null))
        }
    }
}