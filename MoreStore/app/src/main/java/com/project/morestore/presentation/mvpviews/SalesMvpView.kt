package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.*
import com.project.morestore.data.models.cart.CartItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface SalesMvpView: MvpView {


    @OneExecution
    fun onDealPlaceAccepted()

    @OneExecution
    fun onDealStatusChanged()

    @OneExecution
    fun onError(message: String)

    @OneExecution
    fun onSalesLoaded(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>, dialogs: List<DialogWrapper>)



    @OneExecution
    fun onItemsLoaded(cartItems: List<CartItem>, activeOrders: List<Order>, activeSales: List<Order>, inactiveOrders: List<Order>, inactiveSales: List<Order>)


}
