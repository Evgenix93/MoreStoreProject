package com.project.morestore.fragments.orders.create

import android.content.Context
import com.project.morestore.R
import com.project.morestore.models.*
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.SalesRepository
import com.project.morestore.util.MessageActionType
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class OrderCreatePresenter(context: Context)
    : MvpPresenter<OrderCreateView>() {
    private val orderRepository = OrdersRepository(context)
    private val salesRepository = SalesRepository()
    private val chatRepository = ChatRepository(context)

    ///////////////////////////////////////////////////////////////////////////
    //                      public
    ///////////////////////////////////////////////////////////////////////////

    fun onBackClick() {
        viewState.navigate(null)
    }

    fun setProduct(product: Product) {
        viewState.setProductInfo(product)
    }

    fun onCancelOrderCreateClick() {
        viewState.navigate(null)
    }

    fun onCreateOrder(cartId: Long, delivery: Int, place: OrderPlace, pay: Int, fromChat: Boolean, product: Product){
        presenterScope.launch {
            viewState.loading()
            if(place.id.toInt() == OrderCreateFragment.PLACE_FROM_ME && place.address == null){
                viewState.showMessage("Укажите адрес сделки")
                return@launch
            }
            if(place.id.toInt() == OrderCreateFragment.PLACE_FROM_ME && place.date == 0L){
                viewState.showMessage("Укажите время сделки")
                return@launch
            }

            val newOrder = NewOrder(
                cart = listOf(cartId),
                delivery = delivery,
                place = place,
                pay = pay
            )
            val response = orderRepository.createOrder(newOrder)
            when(response?.code()){
                200 -> {
                    viewState.showMessage("Заказ оформлен")
                    val order = getAllOrders()?.find { it.idCart.find { cart -> cart == cartId } != null }
                    if(order != null && place.address != null)
                    addDealPlace(order.id, place.address )
                    if(fromChat)
                        viewState.navigate(R.id.chatFragment)
                    else {
                        createBuyDialog(userId = product.idUser!!, productId = product.id)
                        viewState.navigate(R.id.ordersActiveFragment)
                    }
                }
                400 -> viewState.showMessage(getStringFromResponse(response.errorBody()!!))
                null -> viewState.showMessage("нет интернета")
                500 -> viewState.showMessage("500 Internal Server Error")

            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    private fun getStringFromResponse(response: ResponseBody): String{
        return  response.string()

    }

    private suspend fun getAllOrders(): List<Order>?{
        val response = orderRepository.getAllOrders()
        return when(response?.code()){
            200 -> response.body()
            null -> {
                viewState.showMessage("нет интернета")
                null
            }
            400 -> {
                viewState.showMessage(response.errorBody()!!.string())
                null
            }
            500 -> {
                viewState.showMessage("500 Internal Server Error")
                null
            }
            else -> null

        }


    }

    private suspend fun addDealPlace(orderId: Long, placeAddress: String): Boolean{
        val response = salesRepository.addDealPlace(orderId, placeAddress)
        return when(response?.code()){
            200 -> true
            null -> {
                viewState.showMessage("нет интернета")
                false
            }
            400 -> {
                viewState.showMessage(response.errorBody()!!.string())
                false
            }
            500 -> {
                viewState.showMessage("500 Internal Server Error")
                false
            }
            else -> false

        }

    }

    private suspend fun sendSuspendBuyRequest(dialogId: Long){
        val response = chatRepository.sendBuyRequest(ChatFunctionInfo(dialogId = dialogId))
        when (response?.code()) {
            200 -> {

            }
            400 -> {
                val bodyString = getStringFromResponse(response.errorBody()!!)
                viewState.showMessage(bodyString)
            }
            500 -> viewState.showMessage("500 Internal Server Error")
            null -> viewState.showMessage("нет интернета")
            else -> viewState.showMessage("ошибка")

        }


    }

    private suspend fun createBuyDialog(userId: Long, productId: Long) {
        val response = chatRepository.createDialog(userId, productId)
            when (response?.code()) {
                200 -> {
                    sendSuspendBuyRequest(response.body()?.id!!)
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.showMessage(bodyString)
                }
                500 -> viewState.showMessage("500 Internal Server Error")
                null -> viewState.showMessage("нет интернета")
                else -> viewState.showMessage("ошибка")

            }


    }

    fun getSupportDialog() {
        presenterScope.launch {
            val response = chatRepository.getDialogs()
            when (response?.code()) {
                200 -> {
                    val chats = response.body()?.filter { dialogWrapper ->
                        dialogWrapper.dialog.user.id == 1L

                    }?.map {
                        Chat.Support(
                            it.dialog.id,
                            "Служба поддержки",
                            "Помощь с товаром"
                        )
                    }
                    if(chats != null)
                        viewState.supportDialogLoaded(chats.first())
                }
            }
        }
    }

}