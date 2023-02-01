package com.project.morestore.presentation.fragments.orders.create


import android.content.Context
import com.project.morestore.R
import com.project.morestore.data.models.*
import com.project.morestore.data.repositories.*

import com.project.morestore.util.errorMessage
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

import java.util.*
import javax.inject.Inject

class OrderCreatePresenter @Inject constructor(
    @ActivityContext private val context: Context,
    private val orderRepository: OrdersRepository,
    private val salesRepository: SalesRepository,
    private val chatRepository: ChatRepository,
    private val geoRepository: GeoRepository,
    private val userRepository: UserRepository
)
    : MvpPresenter<OrderCreateView>() {

    private var currentPromo: String? = null

    fun getCurrentUserGeoPosition(){
        val address = userRepository.getCurrentUserAddress()
        address ?: return
        viewState.geoPositionLoaded(address)
    }

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
                      ){
        presenterScope.launch {
            viewState.loading()
            if(place.id.toInt() == OrderCreateFragment.PLACE_FROM_ME && place.address == null){
                viewState.showMessage(context.getString(R.string.write_deal_address))
                return@launch
            }
            if(place.id.toInt() == OrderCreateFragment.PLACE_FROM_ME && place.date == 0L){
                viewState.showMessage(context.getString(R.string.write_deal_time))
                return@launch
            }

            val newOrder = NewOrder(
                cart = listOf(cartId),
                delivery = delivery,
                place = place,
                pay = pay,
                comment = comment,
                promocode = currentPromo
            )

            val successBuyRequest = createBuyDialog(userId = product.idUser!!, productId = product.id)
            if(!successBuyRequest) return@launch


            val response = orderRepository.createOrder(newOrder)
            when(response?.code()){
                200 -> {
                    val updatedOrders = getAllOrders()

                    updatedOrders ?:  run {
                        viewState.navigate(R.id.ordersActiveFragment)
                        return@launch
                    }
                    val order = updatedOrders.find { it.idCart.find { cart -> cart == cartId } != null }

                    viewState.showMessage(context.getString(R.string.order_created))
                    if(order != null && place.address != null && delivery == 1)
                       addDealPlace(order.id, place.address )
                    if(fromChat)
                        viewState.navigate(R.id.chatFragment)
                    else {
                        viewState.orderCreated(orderId = order?.id!!)
                    }
                }
                else -> {
                    viewState.showMessage(errorMessage(response))
                }
            }
        }
    }

    private suspend fun getAllOrders(): List<Order>?{
        val response = orderRepository.getAllOrders()
        return when(response?.code()){
            200 -> response.body()
            null -> {
                viewState.showMessage("нет интернета")
                null
            }
            404 -> emptyList()
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun addDealPlace(orderId: Long, placeAddress: String): Boolean{
        val response = salesRepository.addDealPlace(orderId, placeAddress)
        return when(response?.code()){
            200 -> true
            else -> {
                viewState.showMessage(errorMessage(response))
                false
            }
        }

    }

    private suspend fun sendSuspendBuyRequest(dialogId: Long): Boolean{
        val response = chatRepository.sendBuyRequest(ChatFunctionInfo(dialogId = dialogId))
        return when (response?.code()) {
            200 -> {
                true

            }
            else -> {
                viewState.showMessage(errorMessage(response))
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
                else -> {
                    viewState.showMessage(errorMessage(response))
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
                            context.getString(R.string.support),
                            context.getString(R.string.product_support)
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
                          viewState.showMessage(context.getString(R.string.promo_not_active))
                          viewState.applyPromo(null)
                          return@launch
                      }
                      if(response.body()?.firstOrder == 1 && orders.isNotEmpty()){
                          viewState.showMessage(context.getString(R.string.promo_for_first_order))
                          viewState.applyPromo(null)
                          return@launch
                      }
                      viewState.applyPromo(response.body())
                      currentPromo = code
                  }
                  404 -> {
                      viewState.applyPromo(null)
                      viewState.showMessage(context.getString(R.string.promo_not_found))
                      currentPromo = null

                  }
                   else -> {
                       viewState.applyPromo(null)
                       viewState.showMessage(errorMessage(response))
                       currentPromo = null
                   }
              }
          }
    }

    fun getCdekPrice(toAddress: String, product: Product, toPickUpPoint: Boolean){
        presenterScope.launch {
        viewState.loading()
            val dimensions = ProductDimensions(
                length = product.packageDimensions.length,
                width = product.packageDimensions.width,
                height = product.packageDimensions.height,
                weight = (product.packageDimensions.weight!!.toFloat() * 1000).toInt().toString()
            )
            val isCdekPickupPostamat = toAddress.contains("cdek code postamat:")
            val info = CdekCalculatePriceInfo(
                tariff_code = if(toPickUpPoint && !isCdekPickupPostamat) 136
                              else if(toPickUpPoint && isCdekPickupPostamat) 368
                              else 137,
                from_location = AddressString(product.addressCdek?.substringBefore("cdek code:") ?: ""),
                to_location = AddressString(toAddress.substringBefore(if(!isCdekPickupPostamat) "cdek code:" else "cdek code postamat:" )),
                packages = dimensions
            )
            val response = orderRepository.getCdekPrice(info)
            when(response?.code()){
                200 -> viewState.setDeliveryPrice(response.body()!!)
                404 -> {
                    viewState.showCdekError()
                    viewState.showMessage(context.getString(R.string.error_getting_price))
                }
                else -> {
                    viewState.showCdekError()
                    viewState.showMessage(errorMessage(response))}
            }
        }
    }

    fun getYandexPrice(toAddress: String, product: Product){
        presenterScope.launch {
            viewState.loading()
            val fromCoords = geoRepository.getCoordsByAddress(product.address?.fullAddress!!)?.body()?.coords
            if(fromCoords == null){
                viewState.showMessage(context.getString(R.string.error_getting_price))
                return@launch
            }
            val toCoords = geoRepository.getCoordsByAddress(toAddress)?.body()?.coords
            if(toCoords == null){
                viewState.showMessage(context.getString(R.string.error_getting_price))
                return@launch
            }

            val items = listOf(YandexItem(
                size = YandexItemSize(
                             height = product.packageDimensions.height!!.toFloat() / 100,
                             width = product.packageDimensions.width!!.toFloat() / 100,
                             length = product.packageDimensions.length!!.toFloat() / 100),
                weight = product.packageDimensions.weight!!.toFloat()))
            val routePoints = listOf(
                YandexPoint(listOf(fromCoords.lon, fromCoords.lat)),
                YandexPoint(listOf(toCoords.lon, toCoords.lat))
            )
            val info = YandexPriceCalculateInfo(
                items = items,
                routePoints = routePoints
            )
            val response = orderRepository.getYandexGoPrice(info)
            when(response?.code()){
                200 -> viewState.setDeliveryPrice(
                    DeliveryPrice(0f,
                        0,
                        0,
                        0,
                        0,
                        response.body()!!.price.toFloat()))
                else -> {
                    viewState.showMessage(errorMessage(response))
                    viewState.setDeliveryPrice(null)
                }
            }
        }
    }

    private suspend fun createYandexOrder(fromCoords: Coords, toCoords: Coords, toAddress: String, product: Product): YandexOrderInfoBody?{
        val order = YandexGoOrder(
            idOrder = Random().nextLong(),
            comment = "comment",
            emergencyContactName = "name",
            emergencyContactPhone = "+79998887766",
            itemQuantity = "1",
            itemsProductName = product.name,
            productPrice = product.priceNew.toString(),
            pointAddressCoordinates = listOf(toCoords.lon, toCoords.lat),
            pointContactEmail = "email@gmail.com",
            pointContactName = "toName",
            pointContactPhone = "+79998887766",
            pointFullName = toAddress,
            takePointCoordinates = listOf(fromCoords.lon, fromCoords.lat),
            takePointContactEmail = "email@gmail.com",
            takePointContactName = "takeName",
            takePointContactPhone = "+79998887766",
            takePointFullName = product.address?.fullAddress.orEmpty(),
            itemWeight = product.packageDimensions.weight!!.toFloat()
        )
        val response = orderRepository.createYandexGoOrder(order)
        return when(response?.code()){
            200 -> response.body()
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }
}