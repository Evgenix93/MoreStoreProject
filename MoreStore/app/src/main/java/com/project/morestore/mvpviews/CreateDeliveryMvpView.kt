package com.project.morestore.mvpviews

import com.project.morestore.data.models.Product
import com.project.morestore.data.models.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface CreateDeliveryMvpView: MvpView {

    @OneExecution
    fun setProfileInfo(user: User)

    @OneExecution
    fun loading(loading: Boolean)

    @OneExecution
    fun showMessage(message: String)

    @OneExecution
    fun success()

    @OneExecution
    fun setProductInfo(product: Product)
}