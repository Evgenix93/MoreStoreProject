package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.Chat
import com.project.morestore.data.models.Order
import com.project.morestore.data.models.PaymentUrl
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.cart.OrderItem
import com.project.morestore.data.models.cart.OrderStatus
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface OrderDetailsView: MvpView {
    @OneExecution
    fun showMessage(message: String)
    @OneExecution
    fun loading(loading: Boolean)

    @OneExecution
    fun orderStatusChanged(status: OrderStatus)
    @OneExecution
    fun productLoaded(product: Product)
    @OneExecution
    fun orderItemLoaded(orderItem: OrderItem)
    @OneExecution
    fun setProfileInfo(avatar: String, name: String)

    @OneExecution
    fun supportDialogLoaded(chat: Chat)
    @OneExecution
    fun payment(paymentUrl: PaymentUrl, orderId: Long)
    @OneExecution
    fun setFinalPrice(price: Float)
    @OneExecution
    fun navigateToCreateDelivery(order: Order)
}