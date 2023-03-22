package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

@OneExecution
interface CabinetMvpView: MvpView {

    fun showSalesCount(activeSalesCount:Int, finishedSalesCount:Int)
    fun showOrdersCount(activeOrdersCount:Int, finishedOrdersCount:Int)
}