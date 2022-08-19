package com.project.morestore.presenters

import com.project.morestore.models.AddressType
import com.project.morestore.models.MyAddress
import com.project.morestore.repositories.AddressesRepository
import kotlinx.coroutines.launch
import moxy.presenterScope

class EditMyAddressPickupPresenter(
    val myAddress :MyAddress,
    addressNetwork :AddressesRepository
) :MyAddressPickupPresenter(addressNetwork) {

    init {
        isDefault = myAddress.favorite
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showFullname(myAddress.name)
        viewState.showPhone(myAddress.phone)
    }

    override fun save(fullname :String, phoneNumber :String){
        presenterScope.launch(displayError) {
            waitingDelegate.show()
            addressNetwork.editAddress(
                MyAddress(
                    myAddress.id,
                    phoneNumber,
                    myAddress.address,
                    myAddress.cdekCode,
                    fullname,
                    isDefault,
                    AddressType.CDEK.id
                )
            )
            waitingDelegate.hide()
            viewState.back()
        }
    }

    fun delete(){
        viewState.showConfirmDelete()
    }

    fun confirmDelete(){
        presenterScope.launch(displayError) {
            waitingDelegate.show()
            addressNetwork.deleteAddress(myAddress)
            waitingDelegate.hide()
            viewState.back()
        }
    }
}