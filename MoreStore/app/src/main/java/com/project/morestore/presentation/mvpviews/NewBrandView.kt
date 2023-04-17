package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface NewBrandView :MvpView {
    fun loading(show :Boolean = true)
    fun finish(brandId: Long, brandName: String)
    fun showMessage(message: String)
}