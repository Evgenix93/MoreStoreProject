package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.*
import com.project.morestore.data.models.cart.CartItem
import moxy.viewstate.strategy.alias.OneExecution

interface SalesMvpView: SalesDealPlaceMvpView {

    @OneExecution
    fun onSalesLoaded(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>, dialogs: List<DialogWrapper>)



    @OneExecution
    fun onItemsLoaded(cartItems: List<CartItem>, activeOrders: List<Order>, activeSales: List<Order>, inactiveOrders: List<Order>, inactiveSales: List<Order>)


}
