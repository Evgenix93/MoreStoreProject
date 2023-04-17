package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface EditFavoriteSearchView: BaseMvpView {
    @OneExecution
    fun success()
}