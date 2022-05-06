package com.project.morestore.presenters

import com.project.morestore.models.AddressType
import com.project.morestore.models.DeliveryAddress
import com.project.morestore.models.MyAddressData
import com.project.morestore.repositories.AddressesRepository
import com.project.morestore.repositories.UserNetworkGateway
import kotlinx.coroutines.launch
import moxy.presenterScope

class CreateMyAddressPickupPresenter(
    override val address :DeliveryAddress,
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

    override fun save(fullname :String, phoneNumber :String){
        presenterScope.launch(displayError) {
            waitingDelegate.show()
            addressNetwork.createAddress(
                MyAddressData(
                    fullname,
                    phoneNumber.replace("\\D", ""),
                    address,
                    isDefault,
                    AddressType.CDEK.id
                )
            )
            waitingDelegate.hide()
            viewState.back()
        }
    }
}