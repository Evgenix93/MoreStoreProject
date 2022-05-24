package com.project.morestore.mvpviews

import com.project.morestore.models.cart.OrderStatus
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface OrderDetailsView: MvpView {

    @OneExecution
    fun loading(isLoading: Boolean)

    @OneExecution
    fun orderStatusChanged(status: OrderStatus)


    @OneExecution
    fun onError(message: String)
}