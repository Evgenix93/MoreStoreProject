package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.DialogWrapper
import com.project.morestore.data.models.OfferedOrderPlace
import com.project.morestore.data.models.Order
import com.project.morestore.data.models.User
import com.project.morestore.data.models.cart.CartItem
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface DealPlaceMvpView: LoadingMvpView {

    @OneExecution
    fun onDealPlaceAdded()
}