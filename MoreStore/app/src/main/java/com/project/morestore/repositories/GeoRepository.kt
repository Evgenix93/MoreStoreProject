package com.project.morestore.repositories

import com.project.morestore.GeoPosition
import com.project.morestore.models.Address
import com.project.morestore.singletones.Network
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class GeoRepository {
    private val network = Network.geo

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