package com.project.morestore.fragments.orders.create

import com.project.morestore.models.Chat
import com.project.morestore.models.Product
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface OrderCreateView : MvpView {

    @OneExecution
    fun navigate(pageId: Int?)

    @AddToEndSingle
    fun setProductInfo(product: Product)

    @OneExecution
    fun loading()

    @OneExecution
    fun showMessage(message: String)

    @OneExecution
    fun supportDialogLoaded(chat: Chat)
}