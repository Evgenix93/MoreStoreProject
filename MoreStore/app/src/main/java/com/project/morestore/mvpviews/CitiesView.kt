package com.project.morestore.mvpviews

import com.project.morestore.models.Region
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface CitiesView : MvpView {
    fun loading(show :Boolean = false)
    fun showMessage(message :String)
    fun showCities(cities :Array<Region>)
}