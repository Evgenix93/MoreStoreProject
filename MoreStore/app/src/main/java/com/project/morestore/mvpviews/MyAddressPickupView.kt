package com.project.morestore.mvpviews

import com.project.morestore.widgets.loading.LoadingView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface MyAddressPickupView :LoadingView{
    fun showFullname(fullname :String)
    fun showPhone(phoneNumber :String)
    fun showIsDefault(isDefault :Boolean)
    fun showMessage(message :String)
    fun showConfirmDelete()
    fun back()
}