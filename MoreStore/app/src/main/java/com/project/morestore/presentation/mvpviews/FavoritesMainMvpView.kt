package com.project.morestore.presentation.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface FavoritesMainMvpView: MainMvpView {

    @OneExecution
    fun isGuest()
}