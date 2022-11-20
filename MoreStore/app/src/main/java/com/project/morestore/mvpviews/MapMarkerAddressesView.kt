package com.project.morestore.mvpviews

import com.project.morestore.data.models.SuggestAddress
import com.yandex.mapkit.geometry.Point
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface MapMarkerAddressesView :MvpView {
    fun showAddresses(addresses :Array<SuggestAddress>)
    fun showList()
    fun navigateMap(point :Point)
    fun showMessage(message: String)
}