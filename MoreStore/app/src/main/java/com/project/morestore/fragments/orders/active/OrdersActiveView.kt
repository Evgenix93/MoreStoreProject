package com.project.morestore.fragments.orders.active

import com.project.morestore.adapters.cart.OrdersAdapter
import com.project.morestore.dialogs.YesNoDialog
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
}