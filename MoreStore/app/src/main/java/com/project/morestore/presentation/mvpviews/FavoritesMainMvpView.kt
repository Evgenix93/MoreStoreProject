package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface FavoritesMainMvpView: ResultLoadedMvpView {

    @OneExecution
    fun isGuest()

}