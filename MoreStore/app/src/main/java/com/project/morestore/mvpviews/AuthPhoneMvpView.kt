package com.project.morestore.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface AuthPhoneMvpView: AuthMvpView {

    @OneExecution
    fun successNewCode(result: Any)
}