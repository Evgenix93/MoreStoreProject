package com.project.morestore.mvpviews

import com.project.morestore.models.Product
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface OrderDetailsView: MvpView {
    @OneExecution
    fun showMessage(message: String)
    @OneExecution
    fun loading(loading: Boolean)

    @OneExecution
    fun orderStatusChanged(status: OrderStatus)
    @OneExecution
    fun productLoaded(product: Product)
    @OneExecution
    fun orderItemLoaded(orderItem: OrderItem)
}