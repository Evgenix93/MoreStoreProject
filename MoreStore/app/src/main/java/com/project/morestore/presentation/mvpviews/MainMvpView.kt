package com.project.morestore.presentation.mvpviews

import com.project.morestore.data.models.SuggestionModels
import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface MainMvpView: LoadingMvpView, ResultLoadedMvpView {
    @OneExecution
    fun success()

    @OneExecution
    fun showOnBoarding()

    @OneExecution
    fun loginFailed()

    @OneExecution
    fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>)
}