package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.DialogWrapper
import com.project.morestore.data.models.OfferedOrderPlace
import com.project.morestore.data.models.Order
import com.project.morestore.data.models.User
import com.project.morestore.data.models.cart.CartItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface SalesHistoryMvpView: MvpView {

    @OneExecution
    fun onError(message: String)

    @OneExecution
    fun onSalesLoaded(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>, dialogs: List<DialogWrapper>)



    @OneExecution
    fun onItemsLoaded(cartItems: List<CartItem>, activeOrders: List<Order>, activeSales: List<Order>, inactiveOrders: List<Order>, inactiveSales: List<Order>)


}