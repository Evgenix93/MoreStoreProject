package com.project.morestore.fragments.orders.active

import com.project.morestore.adapters.cart.OrdersAdapter
import com.project.morestore.dialogs.YesNoDialog
import com.project.morestore.models.User
import com.project.morestore.models.cart.OrderItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface OrdersActiveView : MvpView {
    @OneExecution
    fun navigate(pageId: Int?)
    @OneExecution
    fun navigate(productId: Long)
    @AddToEndSingle
    fun initActiveOrders(adapter: OrdersAdapter)
    @OneExecution
    fun showAcceptOrderDialog(acceptDialog: YesNoDialog)
    @OneExecution
    fun showMessage(message: String)
    @OneExecution
    fun loading()
    @OneExecution
    fun navigateToChat(userId: Long, productId: Long)
    @OneExecution
    fun navigate(user: User)
    @OneExecution
    fun navigate(order: OrderItem)
}