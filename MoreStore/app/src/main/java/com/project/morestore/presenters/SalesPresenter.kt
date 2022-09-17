package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.models.*
import com.project.morestore.models.cart.CartItem
import com.project.morestore.mvpviews.SalesMvpView
import com.project.morestore.repositories.*
import com.project.morestore.util.MessageActionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import java.util.*
import kotlin.math.absoluteValue

class SalesPresenter(context: Context): MvpPresenter<SalesMvpView>() {
    private val salesRepository = SalesRepository()
    private val userRepository = UserRepository(context)
    private val ordersRepository = OrdersRepository(context)
    private val authRepository  = AuthRepository(context)
    private val chatRepository = ChatRepository(context)


    fun getSales(isHistory: Boolean){
        presenterScope.launch {
            val response = salesRepository.getSales()
            when(response?.code()){
                200 -> {
                    val sales = response.body()!!.reversed()
                    val addresses = getAddresses()
                    val avatars = sales.map{
                        if(it.idUser == null)
                            null
                        else
                           getUser(it.idUser)
                    }
                    val activeSales = sales.filter{it.status == 0}
                    val inactiveSales = sales.filter{it.status == 1}
                    if(isHistory) {
                     /*   val inactiveSalesSorted = inactiveSales
                            .sortedBy {sale ->
                             val timestamp = addresses.find{address -> address.idOrder == sale.id}
                                    ?.address?.substringAfter(';')?.toLongOrNull()
                                if(timestamp != null)
                                    timestamp - System.currentTimeMillis()
                                else
                                    null
                            }*/

                        viewState.onSalesLoaded(inactiveSales, addresses, avatars, emptyList())
                    }
                    else {
                       /* val activeSalesSorted = activeSales
                            .sortedBy {sale ->
                              val timestamp = addresses.find{address -> address.idOrder == sale.id}
                                    ?.address?.substringAfter(';')?.toLongOrNull()
                                if(timestamp != null)
                                    (timestamp - System.currentTimeMillis())
                                else {
                                    null
                                }
                            }*/
                        val dialogs = getDialogs().reversed()
                       val activeSalesSorted = activeSales.filter { activeSales.find { saleCheck ->
                           saleCheck.id != it.id && saleCheck.cart?.first()?.id == it.cart?.first()?.id &&
                                   saleCheck.idUser == it.idUser && saleCheck.id > it.id } == null &&
                       inactiveSales.find { inactiveSale -> it.cart?.first()?.id == inactiveSale.cart?.first()?.id && it.idUser == inactiveSale.idUser  } == null}.sortedBy { sale ->
                           val address = addresses.find { sale.id == it.idOrder }
                                when{
                                    sale.cart?.first()?.statusUser?.buy == null -> 1
                                    sale.cart.first().statusUser?.buy?.status == 2 -> 3
                                    sale.cart.first().statusUser?.buy?.status == 0 -> 1
                                    address == null -> 1
                                    address.type == OfferedPlaceType.APPLICATION.value && address.status == 0 -> 1
                                    else -> 2
                                }
                            }.map {
                                if(it.idCdek != null && it.delivery == 3){
                                    val info = ordersRepository.getCdekOrderInfo(it.idCdek)?.body()
                                    if(info != null){
                                        it.deliveryStatus = if(info.entity.statuses.first().code == CdekStatuses.INVALID) "Данные доставки некорректны"
                                                            else  info.entity.statuses.first().name

                                    }else it.deliveryStatus = "Неизвестный статус доставки"

                                }
                                if(it.idYandex != null && it.delivery == 2){
                                    val info = ordersRepository.getYandexGoOrderInfo(it.idYandex)?.body()
                                    if(info != null){
                                        it.deliveryStatus = if(info.status == "ready_for_approval") null else YandexDeliveryStatus.statuses[info.status]
                                    }else it.deliveryStatus = "Неизвестный статус доставки"
                                }
                           it
                       }
                        viewState.onSalesLoaded(activeSalesSorted, addresses, avatars, dialogs)
                    }
                    val cartItems = getCartItems() ?: emptyList()
                    val orderItems = getOrderItems()?.filter{it.cart != null}
                    val activeOrders = orderItems?.filter { it.status == 0 } ?: emptyList()
                    val inactiveOrders = orderItems?.filter{it.status == 1}.also{Log.d("Sales", "inactiveOrders = $it")} ?: emptyList()
                    val filteredOrderItems = activeOrders.filter { activeOrders.find { orderCheck -> orderCheck.id != it.id && orderCheck.cart?.first()?.id == it.cart?.first()?.id &&
                            orderCheck.id > it.id } == null &&
                            cartItems.find { cartItem -> cartItem.product.id == it.cart?.first()?.id } == null}
                    val activeSalesFiltered = activeSales.filter { activeSales.find { saleCheck ->
                        saleCheck.id != it.id && saleCheck.cart?.first()?.id == it.cart?.first()?.id &&
                                saleCheck.idUser == it.idUser && saleCheck.id > it.id } == null }
                    viewState.onItemsLoaded(cartItems, filteredOrderItems, activeSalesFiltered, inactiveOrders, inactiveSales)
                }
                400 -> {viewState.onError(getError(response.errorBody()!!))}
                404 -> {
                    viewState.onSalesLoaded(emptyList(), emptyList(), emptyList(), emptyList())
                    val cartItems = getCartItems() ?: emptyList()
                    val orderItems = getOrderItems()?.filter{it.cart != null}
                    val activeOrders = orderItems?.filter { it.status == 0 } ?: emptyList()
                    val inactiveOrders = orderItems?.filter{it.status == 1}.also{Log.d("Sales", "inactiveOrders = $it")} ?: emptyList()
                    viewState.onItemsLoaded(cartItems, activeOrders, emptyList(), inactiveOrders, emptyList())
                }
                0 -> {viewState.onError(getError(response.errorBody()!!))}
                null -> {viewState.onError("Нет интернета")}
            }
        }
    }

    fun addDealPlace(orderId: Long, address: String){
        presenterScope.launch{
            val response = salesRepository.addDealPlace(orderId, address)
            when(response?.code()){
                200 -> {
                    if(response.body()!!)
                    viewState.onDealPlaceAdded()
                    else
                        viewState.onError("Ошибка при добавлении адреса")
                }
                400 -> {viewState.onError(getError(response.errorBody()!!))}
                null -> {viewState.onError("Нет интернета")}
            }
        }
        }

   private suspend fun getUser(id: Long): User? {
       val response = userRepository.getUser(id)
      return when(response?.code()){
           200 -> response.body()!!
           else -> null
       }
   }

    private suspend fun getAddresses(): List<OfferedOrderPlace>{
        val response = ordersRepository.getOrderAddresses()
       return when(response?.code()){
            200 -> response.body()!!
            else -> emptyList()
        }
    }

     fun acceptOrderPlace(offeredOrderPlaceChange: OfferedOrderPlaceChange){
        presenterScope.launch {
            val response = ordersRepository.changeOrderPlaceStatus(offeredOrderPlaceChange)
            when(response?.code()){
                200 -> {
                    viewState.onDealPlaceAccepted()
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

    private suspend fun getCartItems(): List<CartItem>?{
        val response = ordersRepository.getCartItems(authRepository.getUserId())
        return if(response?.code() == 200 ) response.body() else null

    }

    private suspend fun getOrderItems(): List<Order>?{
        val response = ordersRepository.getAllOrders()
        return if(response?.code() == 200 ) response.body() else null

    }

    private suspend fun getDialogs(): List<DialogWrapper>{
        val response = chatRepository.getDialogs()
        return if(response?.code() == 200) response.body()!! else emptyList()
    }

    fun submitBuy(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.submitBuy(info)
            when(response?.code()){
                200 -> viewState.onDealStatusChanged()
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
                    viewState.onDealStatusChanged()
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