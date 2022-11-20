package com.project.morestore.fragments.orders.create

import com.project.morestore.data.models.*
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface OrderCreateView : MvpView {

    @OneExecution
    fun navigate(pageId: Int?)

    @OneExecution
    fun orderCreated(orderId: Long)

    @AddToEndSingle
    fun setProductInfo(product: Product)

    @OneExecution
    fun loading()

    @OneExecution
    fun showMessage(message: String)

    @OneExecution
    fun supportDialogLoaded(chat: Chat)

    @OneExecution
    fun payForOrder(paymentUrl: PaymentUrl, orderId: Long)

    @OneExecution
    fun setDeliveryPrice(price: DeliveryPrice?)

    @OneExecution
    fun showCdekError()

    @OneExecution
    fun applyPromo(promo: PromoCode?)

    @OneExecution
    fun geoPositionLoaded(address: Address)
}