package com.project.morestore.presentation.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface RegistrationMvpView: UserMvpView {

    @OneExecution
    fun successNewCode()
}