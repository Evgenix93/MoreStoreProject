package com.project.morestore.presenters

import com.project.morestore.models.CdekAddress
import com.project.morestore.models.DeliveryAddress
import com.project.morestore.mvpviews.MyAddressPickupView
import com.project.morestore.repositories.AddressesRepository
import com.project.morestore.widgets.loading.LoadingPresenter
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.MvpPresenter

abstract class MyAddressPickupPresenter(
    protected val addressNetwork :AddressesRepository
) : MvpPresenter<MyAddressPickupView>() {

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

    protected fun mapAddress(cdek :CdekAddress) :DeliveryAddress{
        val address = cdek.location
        val addressParts = address.address.split(", ")
        return DeliveryAddress(
            address.city ?: "",
            addressParts[0],
            address.index ?: "",
            addressParts[1],
            addressParts.find { it.startsWith("корп.") }?.substringAfter('.'),
            addressParts.find { it.startsWith("стр.") }?.substringAfter('.'),
            null
        )
    }
}