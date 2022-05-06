package com.project.morestore.fragments.orders.cart

import com.project.morestore.adapters.cart.CartAdapter
import com.project.morestore.dialogs.DeleteDialog
import com.project.morestore.models.Product
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface OrdersCartView : MvpView {
    @OneExecution
    fun navigate(product: Product)
    @AddToEndSingle
    fun initCart(adapter: CartAdapter)
    @OneExecution
    fun error(s: String)
    @OneExecution
    fun showDeleteDialog(deleteDialog: DeleteDialog)
}