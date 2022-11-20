package com.project.morestore.mvpviews

import moxy.viewstate.strategy.alias.OneExecution

interface MainFragmentMvpView: CatalogMvpView {

    @OneExecution
    fun showOnBoarding()

    @OneExecution
    fun loginFailed()


}