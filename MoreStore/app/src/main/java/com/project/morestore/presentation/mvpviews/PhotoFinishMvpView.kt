package com.project.morestore.presentation.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface PhotoFinishMvpView: BaseMvpView {

    @OneExecution
    fun success()
}