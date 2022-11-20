package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import kotlin.reflect.KClass

interface FavoritesMvpView: MainMvpView {

    @OneExecution
    fun emptyList()

}