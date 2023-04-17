package com.project.morestore.domain.presenters

import com.project.morestore.Geolocator
import com.project.morestore.data.models.SuggestAddress
import com.project.morestore.presentation.mvpviews.MapMarkerAddressesView
import com.project.morestore.util.SimpleSearchListener
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.Address.Component.Kind.*
import com.yandex.runtime.Error
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class MapMarkerAddressesPresenter @Inject constructor(
    private val searchManager :SearchManager,
    private val geolocator: Geolocator
) :MvpPresenter<MapMarkerAddressesView>() {
    private val markerPointListener = object :SimpleSearchListener{
        override fun onSearchResponse(p0: Response) {
            showResult(p0.collection.children)
        }
    }
    private val searchListener = object :Session.SearchListener{
        override fun onSearchResponse(p0: Response) {
            p0.collection.children
                .also {
                    navigateMap(it)
                    showResult(it)
                }
        }

        override fun onSearchError(p0: Error) {
            viewState.showMessage(p0.toString())
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        moveToCurrentPosition()
    }

    fun moveToCurrentPosition(){
        presenterScope.launch {
            geolocator.getCurrentPosition()?.let {
                viewState.navigateMap(Point(it.lat, it.lon))
                onMarkerMove(it.lat, it.lon)
            }
        }
    }

    fun onMarkerMove(lat :Double, lon :Double){ presenterScope.launch {
        searchManager.submit(Point(lat, lon), 16, SearchOptions(), markerPointListener)
    }}

    fun onSearch(searchLine :String, searchArea :Geometry){ presenterScope.launch {
        searchManager.submit(searchLine, searchArea, SearchOptions(), searchListener)
    }}

    private fun showResult(items :List<GeoObjectCollection.Item>){
        items.mapNotNull(::addressTransform)
            .also {
                if(it.isNotEmpty()) viewState.showList()
                viewState.showAddresses(it.toTypedArray())
            }
    }

    private fun addressTransform (geo :GeoObjectCollection.Item) :SuggestAddress? {
        val mapAddress = geo.obj
            ?.metadataContainer
            ?.getItem(ToponymObjectMetadata::class.java)
            ?.address
            ?: return null
        val parts = mapAddress.components
        if(parts.find { it.kinds.contains(HOUSE) } == null) return null

        val country = parts.firstOrNull{ it.kinds.contains(COUNTRY) }?.name
        val city = parts.firstOrNull { it.kinds.contains(LOCALITY) }?.name
        val region = parts.firstOrNull { it.kinds.contains(REGION) }?.name

        return SuggestAddress(
            country ?: "",
            city ?: "",
            parts.firstOrNull { it.kinds.contains(STREET) }?.name ?: "",
            mapAddress.postalCode ?: "",
            parts.firstOrNull { it.kinds.contains(HOUSE) }?.name ?: "",
            "",//todo redundant
            arrayOf(city, region, country).filterNotNull().joinToString { it }
        )
    }

    private fun navigateMap(points :List<GeoObjectCollection.Item>){
        points.mapNotNull { it.obj?.geometry?.firstOrNull()?.point }
            .toTypedArray()
            .also { pts -> pts.firstOrNull()?.also { viewState.navigateMap(it) } }
    }
}