package com.project.morestore.domain.presenters

import com.project.morestore.presentation.mvpviews.MyAddressesView
import com.project.morestore.data.repositories.AddressesRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class MyAddressesPresenter @Inject constructor(
    private val provider :AddressesRepository
) :MvpPresenter<MyAddressesView>() {

    override fun attachView(view :MyAddressesView) {
        super.attachView(view)
        presenterScope.launch {
            provider.getAllAddresses()
                .also{
                    if(it.isEmpty()) viewState.showEmpty()
                    else viewState.showAddresses(it.toTypedArray())
                }
        }
    }
}