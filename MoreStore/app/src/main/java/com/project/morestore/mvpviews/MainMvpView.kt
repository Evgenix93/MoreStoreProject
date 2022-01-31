package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution

interface MainMvpView: MvpView {
    @AddToEnd
    fun loaded(result: Any)

    @OneExecution
    fun loading()

    @OneExecution
    fun error(message: String)

    @OneExecution
    fun showOnBoarding()

    @OneExecution
    fun loadedSuggestions(list: List<String>)

}