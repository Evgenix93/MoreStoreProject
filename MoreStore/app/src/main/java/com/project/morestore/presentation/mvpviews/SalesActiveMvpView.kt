package com.project.morestore.presentation.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface SalesActiveMvpView: SalesMvpView {



    @OneExecution
    fun onDealPlaceAccepted()

    @OneExecution
    fun onDealStatusChanged()
}