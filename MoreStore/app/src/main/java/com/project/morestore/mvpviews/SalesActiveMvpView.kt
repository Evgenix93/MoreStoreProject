package com.project.morestore.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface SalesActiveMvpView: SalesMvpView {



    @OneExecution
    fun onDealPlaceAccepted()

    @OneExecution
    fun onDealStatusChanged()
}