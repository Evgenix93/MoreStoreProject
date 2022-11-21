package com.project.morestore.presentation.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface FavoritesMvpView: MainMvpView {

    @OneExecution
    fun emptyList()

}