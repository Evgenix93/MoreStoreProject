package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface LoadingMvpView: MvpView {

    @OneExecution
    fun loading()

    @OneExecution
    fun error(message: String)
}