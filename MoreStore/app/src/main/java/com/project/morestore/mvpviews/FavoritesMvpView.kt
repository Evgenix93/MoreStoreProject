package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import kotlin.reflect.KClass

interface FavoritesMvpView: MvpView {

    @OneExecution
    fun loading()

    @OneExecution
    fun favoritesLoaded(list: List<*>)

    @OneExecution
    fun error(message: String)

    @OneExecution
    fun emptyList()

    @OneExecution
    fun success()



}