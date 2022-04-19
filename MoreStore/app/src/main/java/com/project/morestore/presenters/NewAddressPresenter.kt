package com.project.morestore.presenters

import android.util.Log
import com.project.morestore.Geolocator
import com.project.morestore.models.Address
import com.project.morestore.models.DeliveryAddress
import com.project.morestore.models.MyAddressData
import com.project.morestore.mvpviews.NewAddressView
import com.project.morestore.repositories.AddressesRepository
import com.project.morestore.repositories.GeoRepository
import com.project.morestore.repositories.UserNetworkGateway
import com.project.morestore.util.isFilled
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class NewAddressPresenter(
    private val geolocator :Geolocator,
    private val geoRepository :GeoRepository,
    private val userProvider :UserNetworkGateway,
    private val addressRepository :AddressesRepository
) :MvpPresenter<NewAddressView>() {
    private val displayError = CoroutineExceptionHandler { _, ex ->
        viewState.showMessage(ex.message ?: "неизвестная ошибка")
    }
    var fullname = ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var phone = ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var city = ""
        set(value){
            field = value
            searchUserCity?.cancel()
            viewState.validForm(validateForm())
        }
    var street = ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var index = ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var house = ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var housing = ""
    var building = ""
    var apartment = ""
    private var searchUserCity :Job? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.requestCity()
        presenterScope.launch { requestUser() }
    }

    fun findCity(){
        searchUserCity = presenterScope.launch(displayError) {
            val TAG = "FindCity"
            Log.d(TAG, "begin")
            val geoPosition = geolocator.getCurrentPosition()

            Log.d(TAG, geoPosition.toString())
            if(geoPosition == null){
                viewState.notFoundCity()
                return@launch
            }
            val address : Address? = geoRepository.getCity(geoPosition)

            Log.d(TAG, address?.toString() ?: "address is null")
            if(address == null){
                viewState.notFoundCity()
                return@launch
            }
            address.fullAddress.substringBefore(',')
                .also {
                    city = it
                    viewState.showCity(it)
                    validateForm()
                }
        }
    }

    fun save(isDefault :Boolean){
        presenterScope.launch(displayError) {
            addressRepository.createAddress(
                MyAddressData(
                    fullname,
                    phone.replace(Regex("\\D"), ""),
                    DeliveryAddress(
                        city,
                        street,
                        index,
                        house,
                        housing.takeIf { it.isFilled() },
                        building.takeIf { it.isFilled() },
                        apartment.takeIf { it.isFilled() }
                    ),
                    isDefault
                )
            )
            viewState.back()
        }
    }

    private suspend fun requestUser(){
        userProvider.getUser()?.let{ user ->
            viewState.showFullname("${user.name} ${user.surname}")
            user.phone?.let { viewState.showPhone(it) }
        }
    }

    private fun validateForm() :Boolean {
        return fullname.isNotEmpty()
                && phone.replace(Regex("\\D"), "").length == 11
                && city.isNotEmpty()
                && street.isNotEmpty()
                && index.length == 6
                && house.isNotEmpty()
    }
}