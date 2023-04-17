package com.project.morestore.presentation.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface AuthPhoneMvpView: AuthMvpView {

    @OneExecution
    fun successNewCode(result: Any)
}