package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution

interface CabinetMvpView: MvpView {
    @OneExecution
    fun isLoggedIn(loggedIn: Boolean)
    @OneExecution
    fun loading(isLoading: Boolean)
    @OneExecution
    fun error(message: String)
    @OneExecution
    fun currentUserLoaded(user: User)
    @AddToEnd
    fun showProductsCounts(countList: List<Int>)
}