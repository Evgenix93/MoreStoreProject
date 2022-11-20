package com.project.morestore.mvpviews

import com.project.morestore.data.models.FeedbackProduct
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface FeedbackProductView :MvpView {
    fun showProducts(products :List<FeedbackProduct>)
}