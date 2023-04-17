package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface Registration5View: LoadingMvpView {

    @OneExecution
    fun success()

    @OneExecution
    fun successNewCode()
}