package com.project.morestore.mvpviews

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

@OneExecution
interface NewAddressView :MvpView {
    fun requestCity()
    fun showFullname(fullname :String)
    fun showPhone(phone :String)
    fun showCity(city :String)
    fun notFoundCity()
    fun validForm(valid :Boolean)
    fun back()
    fun confirmDelete()
    fun showMessage(message :String)
}