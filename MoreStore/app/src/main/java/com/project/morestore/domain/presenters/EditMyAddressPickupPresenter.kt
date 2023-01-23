package com.project.morestore.domain.presenters

import com.project.morestore.data.models.AddressType
import com.project.morestore.data.models.CdekAddress
import com.project.morestore.data.models.MyAddress
import com.project.morestore.data.repositories.AddressesRepository
import kotlinx.coroutines.launch
import moxy.presenterScope
import javax.inject.Inject

class EditMyAddressPickupPresenter @Inject constructor (
    addressNetwork :AddressesRepository
) :MyAddressPickupPresenter(addressNetwork) {


    override fun save(fullname :String, phoneNumber :String, myAddress: MyAddress?, cdekAddress: CdekAddress?){
        if(myAddress == null){
            return
        }
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


    fun confirmDelete(myAddress: MyAddress){
        presenterScope.launch(displayError) {
            waitingDelegate.show()
            addressNetwork.deleteAddress(myAddress)
            waitingDelegate.hide()
            viewState.back()
        }
    }
}