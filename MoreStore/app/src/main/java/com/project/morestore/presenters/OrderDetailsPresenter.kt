package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.models.ChatFunctionInfo
import com.project.morestore.models.OfferedOrderPlace
import com.project.morestore.models.OfferedOrderPlaceChange
import com.project.morestore.models.OfferedPlaceType
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.mvpviews.OrderDetailsView
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.SalesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class OrderDetailsPresenter(context: Context): MvpPresenter<OrderDetailsView>() {
    private val salesRepository = SalesRepository()
    private val ordersRepository = OrdersRepository(context)
    private val chatRepository = ChatRepository(context)

    fun addDealPlace(orderId: Long, address: String){
        presenterScope.launch{
            val response = salesRepository.addDealPlace(orderId, address)
            when(response?.code()){
                200 -> {
                    if(response.body()!!)
                        viewState.orderStatusChanged(OrderStatus.MEETING_NOT_ACCEPTED_SELLER)
                    else
                        viewState.onError("Ошибка при добавлении адреса")
                }
                400 -> {viewState.onError(getError(response.errorBody()!!))}
                null -> {viewState.onError("Нет интернета")}
            }
        }
    }

    fun acceptOrderPlace(offeredOrderPlaceChange: OfferedOrderPlaceChange){
        presenterScope.launch {
            val response = ordersRepository.changeOrderPlaceStatus(offeredOrderPlaceChange)
            when(response?.code()){
                200 -> {
                    viewState.orderStatusChanged(OrderStatus.RECEIVED_SELLER)
                }
                null -> {
                    viewState.onError("нет интернета")

                }
                400 -> {
                    viewState.onError(response.errorBody()!!.string())

                }
                500 -> {
                    viewState.onError("500 Internal Server Error")

                }
                else -> {}

            }
        }
    }

    fun submitBuy(info: ChatFunctionInfo, orderItem: OrderItem){
        presenterScope.launch {
            val response = chatRepository.submitBuy(info)
            when(response?.code()){
                200 -> {
                   when {
                       orderItem.newAddress == null -> viewState.orderStatusChanged(OrderStatus.ADD_MEETING)
                       orderItem.offeredOrderPlace?.type == OfferedPlaceType.PROPOSED.value -> viewState.orderStatusChanged(OrderStatus.MEETING_NOT_ACCEPTED_SELLER)
                       orderItem.offeredOrderPlace?.type == OfferedPlaceType.APPLICATION.value -> viewState.orderStatusChanged(OrderStatus.CHANGE_MEETING_SELLER)
                   }
                }
                400 -> {
                    val bodyString = getError(response.errorBody()!!)
                    viewState.onError(bodyString)
                }
                500 -> viewState.onError("500 Internal Server Error")
                null -> viewState.onError("Ошибка")
            }
        }
    }

    fun cancelBuyRequest(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.cancelBuyRequest(info)
            when (response?.code()) {
                200 -> {
                    viewState.orderStatusChanged(OrderStatus.DECLINED)
                }
                400 -> {
                    val bodyString = getError(response.errorBody()!!)
                    viewState.onError(bodyString)
                }
                500 -> viewState.onError("500 Internal Server Error")
                null -> viewState.onError("нет интернета")
                404 -> viewState.onError("ошибка 404 not found")
                else -> viewState.onError("ошибка")

            }

        }
    }

    private suspend fun getError(errorBody: ResponseBody): String{
        return  withContext(Dispatchers.IO){
            errorBody.string()
        }
    }
}