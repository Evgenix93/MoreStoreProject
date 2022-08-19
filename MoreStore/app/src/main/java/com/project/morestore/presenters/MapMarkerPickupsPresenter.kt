package com.project.morestore.presenters

import com.project.morestore.Geolocator
import com.project.morestore.apis.CdekAddressApi
import com.project.morestore.models.CdekAddress
import com.project.morestore.models.DeliveryAddress
import com.project.morestore.mvpviews.MapMarkerPickupsView
import com.project.morestore.mvpviews.MapMarkerPickupsView.NavigateType.*
import com.project.morestore.util.SimpleSearchListener
import com.project.morestore.util.containsLocation
import com.project.morestore.widgets.loading.LoadingPresenter
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchOptions
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class MapMarkerPickupsPresenter(
    private val searchManager : SearchManager,
    private val geolocator: Geolocator,
    private val region : String,
    private val pickupNetwork :CdekAddressApi
) :MvpPresenter<MapMarkerPickupsView>(){
    private val loadingDelegate = LoadingPresenter()
    private val stubRegion = VisibleRegion(
        Point(0.0, 0.0),
        Point(0.0, 0.0),
        Point(0.0, 0.0),
        Point(0.0, 0.0)
    )
    private val searchRegionStub get() = VisibleRegionUtils.toPolygon(stubRegion)
    private var allAddresses :Array<CdekAddress> = arrayOf()
    private var visibleMap :VisibleRegion = stubRegion
    private var selected :CdekAddress? = null

    private val searchListener = object : SimpleSearchListener{
        override fun onSearchResponse(p0: Response) {
            navigateMap(p0.collection.children)
        }
    }

    override fun attachView(view: MapMarkerPickupsView?) {
        super.attachView(view)
        loadingDelegate.attachView(view)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        searchManager.submit(region, searchRegionStub, SearchOptions(), searchListener)
        presenterScope.launch {
            loadingDelegate.show()
            allAddresses = pickupNetwork.getCdekAddresses()
            showMarkers()
            loadingDelegate.hide()
        }
    }

    fun onGeopositionClick(){
        presenterScope.launch {
            geolocator.getCurrentPosition()?.let {
                viewState.moveMap(Point(it.lat, it.lon), USER)
            }
        }
    }

    fun selectMarker(address :CdekAddress){
        selectAddress(address)
        viewState.showList()
    }

    fun selectAddress(address :CdekAddress){
        selected = address
        viewState.indicateSelected(address)
        viewState.moveMap(Point(address.location.lat, address.location.lon), NONE)
        viewState.enableNext(true)
    }

    fun onMoveMap(region :VisibleRegion){
        visibleMap = region
        showMarkers()
    }

    fun save(){
        viewState.returnAddress(selected!!)
    }

    private fun showMarkers(){
        presenterScope.launch {
            val filtered = async(Default){
                allAddresses.filter {
                    visibleMap.containsLocation(it.location.lat, it.location.lon)
                }.toTypedArray()
            }
            viewState.showAddresses(filtered.await())
        }
    }

    private fun navigateMap(points :List<GeoObjectCollection.Item>){
        points.mapNotNull { it.obj?.geometry?.firstOrNull()?.point }
            .toTypedArray()
            .also { pts -> pts.firstOrNull()?.also { viewState.moveMap(it, CITY) } }
    }

    private fun mapAddress(cdek :CdekAddress) :DeliveryAddress{
        val address = cdek.location
        val addressParts = address.address.split(", ")
        return DeliveryAddress(
            address.city ?: "",
            addressParts[0],
            address.index ?: "",
            addressParts[1],
            addressParts.find { it.startsWith("корп.") }?.substringAfter('.'),
            addressParts.find { it.startsWith("стр.") }?.substringAfter('.'),
            null
        )
    }
}