package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.SingleState

interface UserMvpView: MvpView {

    @OneExecution
    fun success(result: Any)

    @OneExecution
    fun error(message: String)

    @OneExecution
    fun loading()

    @OneExecution
    fun loaded(result: Any)

}