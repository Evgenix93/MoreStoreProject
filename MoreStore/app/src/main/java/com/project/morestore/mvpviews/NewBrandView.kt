package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface NewBrandView :MvpView {
    fun loading(show :Boolean = true)
    fun finish(brand :String, brandId: Long)
    fun showMessage(message: String)
}