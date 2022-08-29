package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.models.*
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.mvpviews.CreateDeliveryMvpView
import com.project.morestore.repositories.GeoRepository
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class CreateDeliveryPresenter(context: Context): MvpPresenter<CreateDeliveryMvpView>() {
    private val userRepository = UserRepository(context)
    private val orderRepository = OrdersRepository(context)
    private val productRepository = ProductRepository(context)
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
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()!!.string())
                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")
                }
                else -> viewState.loading(false)

            }
        }

    }

    fun createCdekOrder(order: Order){
        presenterScope.launch {
            currentUser ?: return@launch
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
            val shipmentPoint2 = order.placeAddress?.substringAfter("cdek code:")
            val items = CdekItems(
                name = order.cart.first().name ?: "",
                ware_key = order.id.toString(),
                cost = order.cart.first().priceNew.toString(),
                weight = (order.cart.first().packageDimensions.weight!!.toFloat() * 1000).toInt().toString(),
                payment = CdekPayment()
            )
            val cdekOrder = CdekOrder(
                id_order = order.id,
                shipment_point1 = shipmenPoin1 ?: "",
                delivery_point1 = shipmentPoint2 ?: "",
                sender = sender,
                recipient = cdekRecipient,
                packages = packages,
                items = items
            )
            val response = orderRepository.createCdekOrder(cdekOrder)
            when (response?.code()){
                200 -> {
                    viewState.loading(false)
                    viewState.success()

                }
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()!!.string())
                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")
                }
                500 -> {
                    viewState.loading(false)
                    viewState.showMessage("500 internal server error")
                }
                else -> viewState.loading(false)
            }


        }


    }

    fun createAndSubmitYandexOrder(order: Order){
        presenterScope.launch {
            viewState.loading(true)
            val fromCoords = geoRepository.getCoordsByAddress("dfd")?.body()?.coords
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
            val yandexOrderId = createYandexOrder(fromCoords, toCoords, order.cart!!.first())
            yandexOrderId ?: return@launch
            val response = orderRepository.submitYandexGoOrder(YandexClaimId(yandexOrderId))

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
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()!!.string())
                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")
                }
                else -> viewState.loading(false)

            }


        }
    }

    private suspend fun getUserById(id: Long): User?{
        val response = userRepository.getUser(id)
        return when(response?.code()){
            200 -> response.body()
            null -> null
            400 -> {
                viewState.showMessage(response.errorBody()!!.string())
                null
            }
            500 -> null
            else -> null
        }
    }

    private suspend fun createYandexOrder(fromCoords: Coords, toCoords: Coords, product: Product): Long?{
        val order = YandexGoOrder(
            idOrder = 1,
            comment = "comment",
            emergencyContactName = "",
            emergencyContactPhone = "",
            itemQuantity = "1",
            itemsProductName = product.name,
            productPrice = product.priceNew.toString(),
            pointAddressCoordinates = "${toCoords.lat}, ${toCoords.lon}",
            pointContactEmail = "",
            pointContactName = "",
            pointContactPhone = "",
            pointFullName = "",
            takePointCoordinates = "${fromCoords.lat}, ${fromCoords.lon}",
            takePointContactEmail = "",
            takePointContactName = "",
            takePointContactPhone = "",
            takePointFullName = ""
        )
        val response = orderRepository.createYandexGoOrder(order)
        return when(response?.code()){
            200 -> 3
            400 -> {
                viewState.loading(false)
                viewState.showMessage(response.errorBody()!!.string())
                null
            }
            null -> {
                viewState.loading(false)
                viewState.showMessage("нет интернета")
                null

            }
            500 -> {
                viewState.loading(false)
                viewState.showMessage("500 internal server error")
                null
            }
            else -> {
                viewState.loading(false)
                null
            }
        }

    }



}