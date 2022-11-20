package com.project.morestore.mvpviews

import com.project.morestore.models.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.SingleState

interface AuthMvpView: MvpView {

    @OneExecution
    fun success(result: Any, extra: Any? = null)
    @OneExecution
    fun error(message: String)
    @OneExecution
    fun loading()

    @OneExecution
    fun registrationComplete(complete: Boolean, user: User)
}