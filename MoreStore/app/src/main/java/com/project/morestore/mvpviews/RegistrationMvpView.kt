package com.project.morestore.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface RegistrationMvpView: UserMvpView {

    @OneExecution
    fun successNewCode()
}