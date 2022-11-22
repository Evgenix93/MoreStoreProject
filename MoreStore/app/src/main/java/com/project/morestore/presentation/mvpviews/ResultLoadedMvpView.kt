package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface ResultLoadedMvpView: MvpView {

    @OneExecution
    fun loaded(result: Any)
}