package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.R
import com.project.morestore.models.ChatFunctionInfo
import com.project.morestore.models.OfferedOrderPlaceChange
import com.project.morestore.models.OrderId
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.mvpviews.OrderDetailsView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.repositories.OrdersRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class OrderDetailsPresenter(context: Context): MvpPresenter<OrderDetailsView>()  {
    private val ordersRepository = OrdersRepository(context)
    private val chatRepository = ChatRepository(context)
    private val authRepository = AuthRepository(context)

     fun acceptOrderPlace(orderId: Long, addressId: Long, address: String, asBuyer: Boolean){
        presenterScope.launch {
            viewState.loading(true)
            val change = OfferedOrderPlaceChange(
                idOrder = orderId,
                idAddress = addressId,
                address = address,
                status = 1)
            val response = ordersRepository.changeOrderPlaceStatus(change)
            when(response?.code()){
                200 -> {
                    viewState.loading(false)
                    viewState.showMessage("Место сделки принято")
                    viewState.orderStatusChanged(if(asBuyer) OrderStatus.RECEIVED else OrderStatus.RECEIVED_SELLER)

                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")

                }
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()!!.string())

                }
                500 -> {
                    viewState.loading(false)
                    viewState.showMessage("500 Internal Server Error")

                }
                else -> {viewState.loading(false)}

            }
        }
    }

     fun submitReceiveOrder(orderId: Long){
        presenterScope.launch {
            val response = ordersRepository.submitReceiveOrder(OrderId(orderId))
            when(response?.code()){
                200 -> {
                    viewState.loading(false)
                    viewState.orderStatusChanged(OrderStatus.RECEIVED_SUCCESSFULLY)
                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")

                }
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()!!.string())

                }
                500 -> {
                    viewState.loading(false)
                    viewState.showMessage("500 Internal Server Error")

                }
                else -> {viewState.loading(false)}

            }

        }
    }

    fun cancelBuyRequest(info: ChatFunctionInfo, sellerId: Long){
        presenterScope.launch {
            viewState.loading(true)
            val response = chatRepository.cancelBuyRequest(info)
            when (response?.code()) {
                200 -> {
                    viewState.loading(false)
                    viewState.orderStatusChanged(if(authRepository.getUserId() == sellerId) OrderStatus.DECLINED else OrderStatus.DECLINED_BUYER)
                }
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()?.string().orEmpty())
                }
                500 -> {
                    viewState.loading(false)
                    viewState.showMessage("500 Internal Server Error")
                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")
                }
                404 -> {
                    viewState.loading(false)
                    viewState.showMessage("ошибка 404 not found")
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage("ошибка")
                }

            }

        }
    }
}