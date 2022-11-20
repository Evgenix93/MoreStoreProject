package com.project.morestore.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface MessagesMvpView: MainMvpView {

    @OneExecution
    fun showDialogCount(type: String, count: Int)

    @OneExecution
    fun showUnreadTab(tab: Int, unread: Boolean)
}