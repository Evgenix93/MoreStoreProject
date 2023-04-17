package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface FavoritesMvpView: LoadingMvpView, ResultLoadedMvpView {

    @OneExecution
    fun emptyList()

}