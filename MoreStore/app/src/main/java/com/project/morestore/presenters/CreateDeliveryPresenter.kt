package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.models.*
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.mvpviews.CreateDeliveryMvpView
import com.project.morestore.repositories.GeoRepository
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class CreateDeliveryPresenter @Inject constructor(
private val userRepository: UserRepository,
private val orderRepository: OrdersRepository,
private val productRepository: ProductRepository): MvpPresenter<CreateDeliveryMvpView>() {

    private val geoRepository = GeoRepository()
    private var currentUser: User? = null

    fun getUserInfo(){
        presenterScope.launch {
            viewState.loading(true)
            val response = userRepository.getCurrentUserInfo()
            when(response?.code()){
                200 ->{
                    currentUser = response.body()
                    viewState.loading(false)
                    viewState.setProfileInfo(response.body()!!)
                }
                else -> {
                    viewState.showMessage(errorMessage(response))
                }
            }
        }
    }

    fun createCdekOrder(order: Order){
        presenterScope.launch {
            currentUser ?: return@launch
            order.placeAddress ?: return@launch
            viewState.loading(true)
            val sender = CdekSender(
                company = currentUser!!.name ?: "",
                name = currentUser!!.name ?: "",
                email = currentUser!!.email ?: "",
                phones = listOf(CdekPhone(currentUser!!.phone ?: "")),

            )
            val recipient = getUserById(order.idUser ?: -1)
            if(recipient == null){
                viewState.loading(false)
                return@launch
            }
            val cdekRecipient = CdekRecipient(
                name = recipient.name ?: "",
                phones = listOf(CdekPhone(recipient.phone ?: "")),
            )
            val packages = CdekPackages(
                number = "1",
                weight = (order.cart?.first()?.packageDimensions?.weight!!.toFloat() * 1000).toInt().toString(),
                length = order.cart.first().packageDimensions.length.toString(),
                width = order.cart.first().packageDimensions.width.toString(),
                height = order.cart.first().packageDimensions.height.toString()

            )
            val shipmenPoin1 = order.cart.first().addressCdek?.substringAfter("cdek code:")
            val isCdekPickupPoint = order.placeAddress.contains("cdek code:")
            val shipmentPoint2 = if(isCdekPickupPoint)
                                    order.placeAddress.substringAfter("cdek code:")
                                 else null
            val location = if(isCdekPickupPoint.not())
                              CdekLocation(address = order.placeAddress)
                           else null
            val tariff = if(isCdekPickupPoint) 136 else 137

            val items = CdekItems(
                name = order.cart.first().name,
                ware_key = order.id.toString(),
                cost = order.cart.first().priceNew.toString(),
                weight = (order.cart.first().packageDimensions.weight!!.toFloat() * 1000).toInt().toString(),
                payment = CdekPayment()
            )
            val cdekOrder = CdekOrder(
                tariff_code = tariff,
                id_order = order.id,
                shipment_point1 = shipmenPoin1 ?: "",
                delivery_point1 = shipmentPoint2,
                sender = sender,
                recipient = cdekRecipient,
                packages = packages,
                items = items,
                toLocation = location
            )
            val response = orderRepository.createCdekOrder(cdekOrder)
            when (response?.code()){
                200 -> {
                    viewState.loading(false)
                    viewState.success()

                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
                }
            }
        }
    }

    fun createAndSubmitYandexOrder(order: Order){
        presenterScope.launch {
            viewState.loading(true)
            val fromCoords = geoRepository.getCoordsByAddress(order.cart?.first()?.address?.fullAddress!!)?.body()?.coords
            if(fromCoords == null){
                viewState.loading(false)
                viewState.showMessage("ошибка оценки стоимости")
                return@launch
            }
            val toCoords = geoRepository.getCoordsByAddress(order.placeAddress ?: "")?.body()?.coords
            if(toCoords == null){
                viewState.loading(false)
                viewState.showMessage("ошибка оценки стоимости")
                return@launch
            }
            val yandexOrderId = createYandexOrder(fromCoords, toCoords, order)
            yandexOrderId ?: return@launch
            delay(5000)
            val response = orderRepository.submitYandexGoOrder(YandexClaimId(yandexOrderId))
            when(response?.code()){
                200 ->{
                    viewState.loading(false)
                    if(response.body()?.code == null)
                        viewState.success()
                    else viewState.showMessage("произошла ошибка")
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
                }
            }

        }
    }

    fun getProductInfoById(id: Long){
        presenterScope.launch {
            viewState.loading(true)
            val response = productRepository.getProducts(productId = id)
            when(response?.code()){
                200 ->{
                    viewState.loading(false)
                    viewState.setProductInfo(response.body()!!.first())
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
                }
            }
        }
    }

    private suspend fun getUserById(id: Long): User?{
        val response = userRepository.getUser(id)
        return when(response?.code()){
            200 -> response.body()
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun createYandexOrder(fromCoords: Coords, toCoords: Coords, order: Order): String?{
        val recipient = getUserById(order.idUser ?: -1)
        val yandexOrder = YandexGoOrder(
            idOrder = order.id,
            comment = order.comment ?: "comment",
            emergencyContactName = currentUser?.name.orEmpty(),
            emergencyContactPhone = currentUser?.phone.orEmpty(),
            itemQuantity = "1",
            itemsProductName = order.cart?.first()?.name.orEmpty(),
            productPrice = order.cart?.first()?.priceNew.toString(),
            pointAddressCoordinates = listOf(toCoords.lon, toCoords.lat),
            pointContactEmail = recipient?.email.orEmpty(),
            pointContactName = recipient?.name.orEmpty(),
            pointContactPhone = recipient?.phone.orEmpty(),
            pointFullName = order.placeAddress!!,
            takePointCoordinates = listOf(fromCoords.lon, fromCoords.lat),
            takePointContactEmail = currentUser?.email.orEmpty(),
            takePointContactName = currentUser?.name.orEmpty(),
            takePointContactPhone = currentUser?.phone.orEmpty(),
            takePointFullName = order.cart?.first()?.address?.fullAddress!!,
            itemWeight = order.cart.first().packageDimensions.weight!!.toFloat()
        )
        val response = orderRepository.createYandexGoOrder(yandexOrder)
        return when(response?.code()){
            200 -> response.body()!!.id
            else -> {
                viewState.loading(false)
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun submitYandexGoOrder(id: String){
        val response = orderRepository.submitYandexGoOrder(YandexClaimId(id))
         when(response?.code()){
            200 -> viewState.success()
            else -> {
                viewState.loading(false)
                viewState.showMessage(errorMessage(response))
            }
        }
    }
}