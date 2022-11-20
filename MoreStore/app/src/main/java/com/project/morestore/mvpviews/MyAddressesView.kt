package com.project.morestore.mvpviews

import com.project.morestore.data.models.MyAddress
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

@OneExecution
interface MyAddressesView :MvpView {
    fun showEmpty()
    fun showAddresses(addresses :Array<MyAddress>)
}