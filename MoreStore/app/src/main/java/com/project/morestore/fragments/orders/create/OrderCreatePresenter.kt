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
import java.math.RoundingMode
import java.text.DecimalFormat

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

    fun onCreateOrder(cartId: Long,
                      delivery: Int,
                      place: OrderPlace,
                      pay: Int,
                      fromChat: Boolean,
                      product: Product,
                      comment: String? = null,
                      promo: String? = null,
                      sum: Float? = null){
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
            var promoInfo: PromoCode? = null

            if(promo != null){
                promoInfo = getPromoInfo(promo)
                promoInfo ?: return@launch

            }

            val newOrder = NewOrder(
                cart = listOf(cartId),
                delivery = delivery,
                place = place,
                pay = pay,
                comment = comment

            )
            val orders = getAllOrders()
            orders ?: return@launch
            if(promoInfo != null){
                if(promoInfo.status == 0){
                    viewState.showMessage("Промокод не активен")
                    return@launch
                }
                if(promoInfo.first_order == 1 && orders.isNotEmpty()){
                    viewState.showMessage("Этот промокод только для первого заказа")
                    return@launch
                }
            }

            val response = orderRepository.createOrder(newOrder)
            when(response?.code()){
                200 -> {
                    val updatedOrders = getAllOrders()

                    updatedOrders ?:  run {
                        viewState.navigate(R.id.ordersActiveFragment)
                        return@launch
                    }
                    val order = updatedOrders.find { it.idCart.find { cart -> cart == cartId } != null }

                    if(pay == 2){
                        var finalSum = sum!!
                         if(promoInfo != null ){
                             if(promoInfo.status == 1 && promoInfo.first_order == 1 && orders.isEmpty())
                                 finalSum -= promoInfo.sum
                             if (promoInfo.status == 1 && promoInfo.first_order == 0)
                                 finalSum -= promoInfo.sum

                         }

                        if(!fromChat)
                           createBuyDialog(userId = product.idUser!!, productId = product.id)
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.DOWN
                        val payUrl = getPayUrl(PayOrderInfo(df.format(finalSum).replace(',', '.').toFloat(), order!!.id ))
                        payUrl ?: run {
                            viewState.navigate(R.id.ordersActiveFragment)
                            return@launch}

                        if(delivery == 1){
                            if(place.address != null)
                                addDealPlace(order.id, place.address)
                        }

                        viewState.payForOrder(payUrl, order.id)
                        return@launch

                    }

                    viewState.showMessage("Заказ оформлен")
                    //val order = getAllOrders()?.find { it.idCart.find { cart -> cart == cartId } != null }
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
            404 -> emptyList()
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

    private suspend fun getPromoInfo(code: String): PromoCode?{
        val response = orderRepository.getPromoInfo(code)
        return when(response?.code()){
            200 -> response.body()
            null -> {
                viewState.showMessage("нет интернета")
                null}
            404 -> {
                viewState.showMessage("промокод не найден")
                null
            }
            else -> null
        }
    }

    private suspend fun getPayUrl(payInfo: PayOrderInfo): PaymentUrl? {
        val response = orderRepository.payForOrder(payInfo)
        return when(response?.code()){
            200 -> response.body()!!
            null -> {
                viewState.showMessage("нет интернета")
                null}
            400 -> {
                viewState.showMessage(response.errorBody()!!.string())
                null
            }
            else -> null
        }

    }

    fun getCdekPrice(){
        presenterScope.launch {
        viewState.loading()
            val response = orderRepository.getCdekPrice()
            when(response?.code()){
                200 -> viewState.setDeliveryPrice(response.body()!!.price)
                400 -> viewState.showMessage(response.errorBody()!!.string())
                null -> viewState.showMessage("нет интернета")
                500 -> viewState.showMessage("500 internal server error")
                else -> {}
            }
        }
    }

    fun getYandexPrice(){
        viewState.setDeliveryPrice(340f)
    }

}