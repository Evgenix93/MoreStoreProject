package com.project.morestore.presenters

import com.project.morestore.models.DeliveryAddress
import com.project.morestore.mvpviews.MyAddressPickupView
import com.project.morestore.repositories.AddressesRepository
import com.project.morestore.widgets.loading.LoadingPresenter
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.MvpPresenter

abstract class MyAddressPickupPresenter(
    protected val addressNetwork :AddressesRepository
) : MvpPresenter<MyAddressPickupView>() {

    abstract val address :DeliveryAddress
    abstract fun save(fullname :String, phoneNumber :String)

    protected val waitingDelegate = LoadingPresenter()
    protected val displayError = CoroutineExceptionHandler { _, ex ->
        viewState.showMessage(ex.message ?: "неизвестная ошибка")
        waitingDelegate.hide()
    }
    protected var isDefault = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showIsDefault(isDefault)
    }

    override fun attachView(view: MyAddressPickupView?) {
        super.attachView(view)
        waitingDelegate.attachView(view)
    }

    fun changeDefault(isDefault :Boolean){
        this.isDefault = isDefault
    }
}