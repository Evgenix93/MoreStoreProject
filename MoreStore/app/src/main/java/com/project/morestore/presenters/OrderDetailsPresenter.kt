package com.project.morestore.presenters

import android.content.Context
import com.project.morestore.R
import com.project.morestore.models.ChatFunctionInfo
import com.project.morestore.models.OfferedOrderPlaceChange
import com.project.morestore.models.OfferedPlaceType
import com.project.morestore.models.OrderId
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.mvpviews.OrderDetailsView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.SalesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class OrderDetailsPresenter(context: Context): MvpPresenter<OrderDetailsView>()  {
    private val ordersRepository = OrdersRepository(context)
    private val chatRepository = ChatRepository(context)
    private val authRepository = AuthRepository(context)
    private val salesRepository = SalesRepository()

     fun acceptOrderPlace(orderId: Long, addressId: Long, address: String, asBuyer: Boolean) {
         presenterScope.launch {
             viewState.loading(true)
             val change = OfferedOrderPlaceChange(
                 idOrder = orderId,
                 idAddress = addressId,
                 address = address,
                 status = 1
             )
             val response = ordersRepository.changeOrderPlaceStatus(change)
             when (response?.code()) {
                 200 -> {
                     viewState.loading(false)
                     viewState.showMessage("Место сделки принято")
                     viewState.orderStatusChanged(if (asBuyer) OrderStatus.RECEIVED else OrderStatus.RECEIVED_SELLER)

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
                 else -> {
                     viewState.loading(false)
                 }
             }
         }
     }




    fun addDealPlace(orderId: Long, address: String){
        presenterScope.launch{
            val response = salesRepository.addDealPlace(orderId, address)
            when(response?.code()){
                200 -> {

                viewState.loading(false)
                    if(response.body()!!)
                        viewState.orderStatusChanged(OrderStatus.MEETING_NOT_ACCEPTED_SELLER)
                    else
                        viewState.showMessage("Ошибка при добавлении адреса")
                }
                400 -> {
                    viewState.showMessage(getError(response.errorBody()!!))
                    viewState.loading(false)
                }
                null -> {
                    viewState.showMessage("Нет интернета")
                    viewState.loading(false)
                }
            }
        }
    }

    /*fun acceptOrderPlace(offeredOrderPlaceChange: OfferedOrderPlaceChange){
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
>>>>>>> origin/main

            }
        }
    }*/


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

    fun cancelBuyRequest(orderItem: OrderItem) {
        presenterScope.launch {
            viewState.loading(true)
            val response = chatRepository.cancelBuyRequest(orderItem.chatFunctionInfo!!)
            when (response?.code()) {
                200 -> {
                    viewState.loading(false)
                    viewState.orderStatusChanged(if (authRepository.getUserId() == orderItem.sellerId) OrderStatus.DECLINED else OrderStatus.DECLINED_BUYER)
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

    fun submitBuy(orderItem: OrderItem){
        presenterScope.launch {
            val response = chatRepository.submitBuy(orderItem.chatFunctionInfo!!)
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
                    viewState.showMessage(bodyString)
                }
                500 -> viewState.showMessage("500 Internal Server Error")
                null -> viewState.showMessage("Ошибка")
            }
        }
    }

    /*fun cancelBuyRequest(info: ChatFunctionInfo){
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
>>>>>>> origin/main

            }

        }
    }*/



    private suspend fun getError(errorBody: ResponseBody): String{
        return  withContext(Dispatchers.IO){
            errorBody.string()
        }
    }

}