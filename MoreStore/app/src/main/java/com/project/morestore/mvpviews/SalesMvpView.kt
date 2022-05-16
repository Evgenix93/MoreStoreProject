package com.project.morestore.mvpviews

import com.project.morestore.models.*
import com.project.morestore.models.cart.CartItem
import com.project.morestore.models.cart.OrderItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface SalesMvpView: MvpView {

    @OneExecution
    fun onSalesLoaded(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>)

    @OneExecution
    fun onError(message: String)

    @OneExecution
    fun onDealPlaceAdded()

    @OneExecution
    fun onDealPlaceAccepted()

    @OneExecution
    fun onItemsLoaded(cartItems: List<CartItem>, activeOrders: List<Order>, activeSales: List<Order>, inactiveOrders: List<Order>, inactiveSales: List<Order>)
}