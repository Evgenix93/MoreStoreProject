package com.project.morestore.repositories

import com.project.morestore.GeoPosition
import com.project.morestore.models.Address
import com.project.morestore.singletones.Network

class GeoRepository {
    private val network = Network.geo

    suspend fun getCity(geoposition :GeoPosition) :Address? {
        return network.decodeCity("${geoposition.lat},${geoposition.lon}").body()
    }
}