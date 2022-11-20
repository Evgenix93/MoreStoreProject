package com.project.morestore.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface FavoritesMainMvpView: MainMvpView {

    @OneExecution
    fun isGuest()
}