package com.project.morestore.domain.presenters

import com.project.morestore.data.models.CdekAddress
import com.project.morestore.data.models.DeliveryAddress
import com.project.morestore.data.models.MyAddress
import com.project.morestore.presentation.mvpviews.MyAddressPickupView
import com.project.morestore.data.repositories.AddressesRepository
import com.project.morestore.presentation.widgets.loading.LoadingPresenter
import kotlinx.coroutines.CoroutineExceptionHandler
import moxy.MvpPresenter

abstract class MyAddressPickupPresenter (
    protected val addressNetwork :AddressesRepository
) : MvpPresenter<MyAddressPickupView>() {

    abstract fun save(fullname :String, phoneNumber :String, myAddress: MyAddress, cdekAddress: CdekAddress)

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

    fun setInfo(myAddress: MyAddress){
        isDefault = myAddress.favorite
        viewState.showFullname(myAddress.name)
        viewState.showPhone(myAddress.phone)
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