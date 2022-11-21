package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface SalesDealPlaceMvpView: MvpView {

    @OneExecution
    fun onDealPlaceAdded()

    @OneExecution
    fun onError(message: String)
}