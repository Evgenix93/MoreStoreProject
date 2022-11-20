package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.SingleState

interface OnBoardingMvpView: MvpView {
    @OneExecution
    fun loading()
    @SingleState
    fun loaded(result: List<Any>)
    @OneExecution
    fun error(message: String)
    @OneExecution
    fun success()

}