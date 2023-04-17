package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface BlackListMvpView: MvpView {

    @OneExecution
    fun onBlackListLoaded(users: List<User>)

    @OneExecution
    fun onBlockUnblockUser()

    @OneExecution
    fun loading(isLoading: Boolean)

    @OneExecution
    fun onEmptyList(isEmpty: Boolean)

    @OneExecution
    fun onError(message: String)
}