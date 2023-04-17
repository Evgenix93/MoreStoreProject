package com.project.morestore.presentation.fragments.orders.cart

import com.project.morestore.presentation.adapters.cart.CartAdapter
import com.project.morestore.presentation.dialogs.DeleteDialog
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.User
import com.project.morestore.presentation.mvpviews.LoadingMvpView
import com.project.morestore.presentation.mvpviews.ResultLoadedMvpView
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution

interface OrdersCartView : LoadingMvpView, ResultLoadedMvpView {
    @OneExecution
    fun navigate(product: Product, cartId: Long)
    @AddToEnd
    fun initCart(adapter: CartAdapter)

    @OneExecution
    fun showDeleteDialog(deleteDialog: DeleteDialog)
    @OneExecution
    fun navigate(user: User)
}