package com.project.morestore.mvpviews

import com.project.morestore.models.Order
import com.project.morestore.models.User
import com.project.morestore.models.cart.OrderItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface SalesMvpView: MvpView {

    @OneExecution
    fun onSalesLoaded(sales: List<Order>)

    @OneExecution
    fun onUserLoaded()

    @OneExecution
    fun onError(message: String)

    @OneExecution
    fun onDealPlaceAdded()
}