package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.models.*
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.mvpviews.CreateDeliveryMvpView
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class CreateDeliveryPresenter(context: Context): MvpPresenter<CreateDeliveryMvpView>() {
    private val userRepository = UserRepository(context)
    private val orderRepository = OrdersRepository(context)
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

    fun createCdekOrder(order: OrderItem){
        presenterScope.launch {
            currentUser ?: return@launch
            viewState.loading(true)
            val sender = CdekSender(
                company = currentUser!!.name ?: "",
                name = currentUser!!.name ?: "",
                email = currentUser!!.email ?: "",
                phones = listOf(currentUser!!.phone ?: ""),
                number = currentUser!!.phone ?: ""
            )
            val recipient = getUserById(order.buyerId ?: -1)
            if(recipient == null){
                viewState.loading(false)
                return@launch
            }
            val cdekRecipient = CdekRecipient(
                name = recipient.name ?: "",
                phones = listOf(recipient.phone ?: ""),
                number = recipient.phone ?: ""

            )
            val packages = CdekPackages(
                number = "1",
                weight = order.product.packageDimensions.weight.toString(),
                length = order.product.packageDimensions.length.toString(),
                width = order.product.packageDimensions.width.toString(),
                height = order.product.packageDimensions.height.toString()

            )
            val shipmenPoin1 = order.product.addressCdek?.substringAfter("cdek code:")
            val shipmentPoint2 = order.cdekYandexAddress?.substringAfter("cdek code:")
            val cdekOrder = CdekOrder(
                id_order = order.id,
                shipment_point1 = shipmenPoin1 ?: "",
                delivery_point1 = shipmentPoint2 ?: "",
                sender = sender,
                recipient = cdekRecipient,
                packages = packages
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

}