package com.project.morestore.presentation.fragments.orders.history

import com.project.morestore.presentation.adapters.cart.OrdersHistoryAdapter
import com.project.morestore.data.models.User
import com.project.morestore.data.models.cart.OrderItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface OrdersHistoryView : MvpView {
    @AddToEndSingle
    fun initOrdersHistory(adapter: OrdersHistoryAdapter)
    @OneExecution
    fun showMessage(message: String)
    @OneExecution
    fun navigate(user: User)
    @OneExecution
    fun navigate(order: OrderItem)
}