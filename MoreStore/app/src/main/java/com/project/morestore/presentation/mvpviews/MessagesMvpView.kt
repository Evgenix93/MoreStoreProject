package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface MessagesMvpView: LoadingMvpView, ResultLoadedMvpView {

    @OneExecution
    fun showDialogCount(type: String, count: Int)

    @OneExecution
    fun showUnreadTab(tab: Int, unread: Boolean)
}