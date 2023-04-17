package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface UserMvpView: LoadingMvpView, ResultLoadedMvpView {

    @OneExecution
    fun success(result: Any)



}