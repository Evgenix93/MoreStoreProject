package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

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