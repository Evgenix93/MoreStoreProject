package com.project.morestore.fragments.orders.history

import com.project.morestore.adapters.cart.OrdersHistoryAdapter
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface OrdersHistoryView : MvpView {
    @AddToEndSingle
    fun initOrdersHistory(adapter: OrdersHistoryAdapter)
}