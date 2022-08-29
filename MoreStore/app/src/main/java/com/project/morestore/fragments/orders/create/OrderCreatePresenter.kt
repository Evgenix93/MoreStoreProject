package com.project.morestore.fragments.orders.create

import android.content.Context
import com.project.morestore.R
import com.project.morestore.models.*
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.repositories.GeoRepository
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
    private val geoRepository = GeoRepository()

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




            val newOrder = NewOrder(
                cart = listOf(cartId),
                delivery = delivery,
                place = place,
                pay = pay,
                comment = comment

            )


            if(!fromChat) {
                val successBuyRequest = createBuyDialog(userId = product.idUser!!, productId = product.id)
                if(!successBuyRequest) return@launch
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
                        /*var finalSum = sum!!
                         if(promoInfo != null ){
                             if(promoInfo.status == 1 && promoInfo.first_order == 1 && orders.isEmpty())
                                 finalSum -= promoInfo.sum
                             if (promoInfo.status == 1 && promoInfo.first_order == 0)
                                 finalSum -= promoInfo.sum

                         }*/


                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.DOWN
                        val payUrl = getPayUrl(PayOrderInfo(df.format(sum).replace(',', '.').toFloat(), order!!.id ))
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

    private suspend fun sendSuspendBuyRequest(dialogId: Long): Boolean{
        val response = chatRepository.sendBuyRequest(ChatFunctionInfo(dialogId = dialogId))
        return when (response?.code()) {
            200 -> {
                true

            }
            400 -> {
                val bodyString = getStringFromResponse(response.errorBody()!!)
                viewState.showMessage(bodyString)
                false
            }
            500 -> {
                viewState.showMessage("500 Internal Server Error")
                false
            }
            null -> {
                viewState.showMessage("нет интернета")
                false
            }
            else -> {
                viewState.showMessage("ошибка")
                false
            }

        }


    }

    private suspend fun createBuyDialog(userId: Long, productId: Long): Boolean {
        val response = chatRepository.createDialog(userId, productId)
            return when (response?.code()) {
                200 -> {
                    sendSuspendBuyRequest(response.body()?.id!!)
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.showMessage(bodyString)
                    false
                }
                500 -> {
                    viewState.showMessage("500 Internal Server Error")
                    false
                }
                null -> {
                    viewState.showMessage("нет интернета")
                    false
                }
                else -> {
                    viewState.showMessage("ошибка")
                    false
                }

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

      fun getPromoInfo(code: String){
          presenterScope.launch {
              viewState.loading()
              val response = orderRepository.getPromoInfo(code)
               when (response?.code()) {
                  200 -> {
                      val orders = getAllOrders()
                      orders ?: return@launch
                      if(response.body()?.status == 0){
                          viewState.showMessage("Промокод не активен")
                          viewState.applyPromo(null)
                          return@launch
                      }
                      if(response.body()?.first_order == 1 && orders.isNotEmpty()){
                          viewState.showMessage("Этот промокод только для первого заказа")
                          viewState.applyPromo(null)
                          return@launch
                      }
                      viewState.applyPromo(response.body())
                  }
                  null -> {
                      viewState.applyPromo(null)
                      viewState.showMessage("нет интернета")

                  }
                  404 -> {
                      viewState.applyPromo(null)
                      viewState.showMessage("промокод не найден")

                  }
                   400 -> {
                       viewState.applyPromo(null)
                       viewState.showMessage(response.errorBody()!!.string())

                   }
                  else -> {
                      viewState.applyPromo(null)

                  }
              }
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

    fun getCdekPrice(toAddress: String, product: Product){
        presenterScope.launch {
        viewState.loading()
            val dimensions = ProductDimensions(
                length = product.packageDimensions.length,
                width = product.packageDimensions.width,
                height = product.packageDimensions.height,
                weight = (product.packageDimensions.weight!!.toFloat() * 1000).toInt().toString()
            )
            val info = CdekCalculatePriceInfo(
                from_location = AddressString(product.addressCdek?.substringBefore("cdek code:") ?: ""),
                to_location = AddressString(toAddress.substringBefore("cdek code:")),
                packages = dimensions
            )
            val response = orderRepository.getCdekPrice(info)
            when(response?.code()){
                200 -> viewState.setDeliveryPrice(response.body()!!)
                400 -> {
                    viewState.setDeliveryPrice(null)
                    viewState.showMessage(response.errorBody()!!.string())
                }
                null -> {
                    viewState.setDeliveryPrice(null)
                    viewState.showMessage("нет интернета")
                }
                500 -> {
                    viewState.setDeliveryPrice(null)
                    viewState.showMessage("500 internal server error")
                }
                404 -> {
                    viewState.setDeliveryPrice(null)
                    viewState.showMessage("ошибка расчета цена")
                }
                else -> {
                    viewState.setDeliveryPrice(null)
                    viewState.showMessage("ошибка расчета цены")}
            }
        }
    }

    fun getYandexPrice(toAddress: String, product: Product){
        //viewState.setDeliveryPrice(DeliveryPrice(500f, 1, 2, 1, 2, 553f))
        presenterScope.launch {
            viewState.loading()
            val fromCoords = geoRepository.getCoordsByAddress("dfd")?.body()?.coords
            if(fromCoords == null){
                viewState.showMessage("ошибка оценки стоимости")
                return@launch
            }
            val toCoords = geoRepository.getCoordsByAddress(toAddress)?.body()?.coords
            if(toCoords == null){
                viewState.showMessage("ошибка оценки стоимости")
                return@launch
            }
            val yandexOrderId = createYandexOrder(fromCoords, toCoords, product)
            yandexOrderId ?: return@launch
            val response = orderRepository.getYandexGoOrderInfo(yandexOrderId)
            when(response?.code()){
                200 -> viewState.setDeliveryPrice(
                    DeliveryPrice(0f,
                    0,
                    0,
                    0,
                    0,
                    300f)
                )
                400 -> {
                    viewState.showMessage(response.errorBody()!!.string())
                }
                500 -> viewState.showMessage("500 internal server error")
                null -> viewState.showMessage("нет интернета")

            }



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
                viewState.showMessage(response.errorBody()!!.string())
                null
            }
            null -> {
                viewState.showMessage("нет интернета")
                null

            }
            500 -> {
                viewState.showMessage("500 internal server error")
                null
            }
            else -> null
        }

    }

}