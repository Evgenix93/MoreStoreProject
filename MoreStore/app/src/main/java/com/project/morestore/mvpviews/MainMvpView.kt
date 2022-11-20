package com.project.morestore.mvpviews

import com.project.morestore.models.SuggestionModels
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEnd
import moxy.viewstate.strategy.alias.OneExecution

interface MainMvpView: MvpView {
    @OneExecution
    fun loaded(result: Any)

    @OneExecution
    fun loading()

    @OneExecution
    fun error(message: String)

    @OneExecution
    fun success()
}