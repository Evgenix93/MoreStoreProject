package com.project.morestore.fragments.orders.history

import com.project.morestore.adapters.cart.OrdersHistoryAdapter
import com.project.morestore.models.User
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
}