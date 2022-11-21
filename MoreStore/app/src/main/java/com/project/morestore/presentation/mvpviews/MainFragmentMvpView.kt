package com.project.morestore.presentation.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface MainFragmentMvpView: CatalogMvpView {

    @OneExecution
    fun showOnBoarding()

    @OneExecution
    fun loginFailed()


}