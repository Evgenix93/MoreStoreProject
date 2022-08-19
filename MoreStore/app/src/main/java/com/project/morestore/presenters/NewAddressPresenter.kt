package com.project.morestore.presenters

import android.util.Log
import com.project.morestore.Geolocator
import com.project.morestore.models.*
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
    private val addressRepository :AddressesRepository,
    private val address : MyAddress? = null
) :MvpPresenter<NewAddressView>() {
    private val displayError = CoroutineExceptionHandler { _, ex ->
        viewState.showMessage(ex.message ?: "неизвестная ошибка")
    }
    var fullname = address?.name ?: ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var phone = address?.phone ?: ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var city = address?.address?.city ?: ""
        set(value){
            field = value
            searchUserCity?.cancel()
            viewState.validForm(validateForm())
        }
    var street = address?.address?.street ?: ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var index = address?.address?.index ?: ""
        set(value){
            field = value
            viewState.validForm(validateForm())
        }
    var house = address?.address?.house ?: ""
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
        address?.favorite?.let { viewState.showFavorite(it) }
        if(address == null) {
            viewState.requestCity()
            presenterScope.launch { requestUser() }
        }
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
            address.fullAddress?.substringBefore(',')
                .also {
                    city = it.orEmpty()
                    viewState.showCity(it.orEmpty())
                    validateForm()
                }
        }
    }

    fun save(isDefault :Boolean){
        presenterScope.launch(displayError) {
            if(address == null) {
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
                        null,
                        isDefault,
                        AddressType.HOME.id
                    )
                )
            } else {
                addressRepository.editAddress(
                    MyAddress(
                        address.id,
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
                        null,
                        fullname,
                        isDefault,
                        AddressType.HOME.id
                    )
                )
            }
            viewState.back()
        }
    }

    fun delete(){
        viewState.confirmDelete()
    }

    fun confirmDelete(){
        presenterScope.launch {
            addressRepository.deleteAddress(address!!)
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