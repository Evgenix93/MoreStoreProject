package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface AddCardMvpView: MvpView {

    @OneExecution
    fun error(message: String)

    @OneExecution
    fun success()


}