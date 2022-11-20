package com.project.morestore.mvpviews

import com.project.morestore.data.models.CdekAddress
import com.project.morestore.widgets.loading.LoadingView
import com.yandex.mapkit.geometry.Point
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface MapMarkerPickupsView :MvpView, LoadingView {
    fun moveMap(point :Point, type :NavigateType)
    fun showAddresses(addresses :Array<CdekAddress>)
    fun indicateSelected(selectAddress :CdekAddress)
    fun enableNext(enable :Boolean)
    fun returnAddress(address :CdekAddress)
    fun showList()
    fun showMessage(message: String)

    enum class NavigateType { USER, CITY, NONE }
}