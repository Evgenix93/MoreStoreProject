package com.project.morestore.presenters

import com.project.morestore.data.models.*
import com.project.morestore.repositories.AddressesRepository
import com.project.morestore.repositories.UserNetworkGateway
import kotlinx.coroutines.launch
import moxy.presenterScope
import javax.inject.Inject

class CreateMyAddressPickupPresenter @Inject constructor(
    private val userProvider :UserNetworkGateway,
    addressNetwork :AddressesRepository
) : MyAddressPickupPresenter(addressNetwork) {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        presenterScope.launch(displayError) {
            userProvider.getUser()?.let{ user ->
                viewState.showFullname("${user.name} ${user.surname}")
                user.phone?.let { viewState.showPhone(it) }
            }
        }
    }

    override fun save(fullname :String, phoneNumber :String, myAddress: MyAddress, cdekAddress: CdekAddress){
        presenterScope.launch(displayError) {
            waitingDelegate.show()
            addressNetwork.createAddress(
                MyAddressData(
                    fullname,
                    phoneNumber.replace("\\D", ""),
                    mapAddress(cdekAddress),
                    cdekAddress.code,
                    isDefault,
                    AddressType.CDEK.id
                )
            )
            waitingDelegate.hide()
            viewState.back()
        }
    }
}