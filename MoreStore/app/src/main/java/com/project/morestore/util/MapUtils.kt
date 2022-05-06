package com.project.morestore.util

import com.yandex.mapkit.map.VisibleRegion
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error

interface SimpleSearchListener :Session.SearchListener{
    override fun onSearchResponse(p0: Response) { /* reserved */ }
    override fun onSearchError(p0: Error) { /* reserved */ }
}

fun VisibleRegion.containsLocation(lat :Double, lon: Double) :Boolean{
    val minLat = minOf(topLeft.latitude, topRight.latitude, bottomLeft.latitude, bottomRight.latitude)
    val minLon = minOf(topLeft.longitude, topRight.longitude, bottomLeft.longitude, bottomRight.longitude)
    val maxLat = maxOf(topLeft.latitude, topRight.latitude, bottomLeft.latitude, bottomRight.latitude)
    val maxLon = maxOf(topLeft.longitude, topRight.longitude, bottomLeft.longitude, bottomRight.longitude)
    return lat in minLat..maxLat && lon in minLon..maxLon
}