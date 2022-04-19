package com.project.morestore.presenters

import com.project.morestore.mvpviews.MyAddressesView
import com.project.morestore.repositories.AddressesRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class MyAddressesPresenter(
    private val provider :AddressesRepository
) :MvpPresenter<MyAddressesView>() {

    override fun attachView(view :MyAddressesView) {
        super.attachView(view)
        presenterScope.launch {
            provider.getAllAddresses()
                .also{
                    if(it.isEmpty()) viewState.showEmpty()
                    else viewState.showAddresses(it)
                }
        }
    }
}