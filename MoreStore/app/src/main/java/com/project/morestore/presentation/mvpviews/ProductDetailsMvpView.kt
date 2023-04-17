package com.project.morestore.presentation.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface ProductDetailsMvpView: BaseMvpView {

    @OneExecution
    fun success()
}