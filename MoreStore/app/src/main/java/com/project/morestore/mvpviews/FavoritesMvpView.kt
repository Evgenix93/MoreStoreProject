package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution
import kotlin.reflect.KClass

@OneExecution
interface FavoritesMvpView: MvpView {


    fun favoritesLoaded(list: List<*>)
    fun error(message: String)
}