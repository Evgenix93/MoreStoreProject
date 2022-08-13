package com.project.morestore.mvpviews

import com.project.morestore.models.PaymentUrl
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

@OneExecution
interface RaiseProductView: MvpView {

    fun loading(isLoading: Boolean)

    fun payment(paymentUrl: PaymentUrl)

    fun error(message: String)
}