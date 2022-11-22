package com.project.morestore.presentation.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface RegistrationMvpView: LoadingMvpView {

    @OneExecution
    fun success()
}