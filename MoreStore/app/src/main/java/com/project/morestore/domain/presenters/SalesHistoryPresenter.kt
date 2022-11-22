package com.project.morestore.domain.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.R
import com.project.morestore.data.models.*
import com.project.morestore.data.models.cart.CartItem
import com.project.morestore.data.repositories.*
import com.project.morestore.presentation.mvpviews.SalesHistoryMvpView
import com.project.morestore.util.errorMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class SalesHistoryPresenter @Inject constructor(@ApplicationContext private val context: Context,
                                                private val salesRepository: SalesRepository,
                                                private val ordersRepository: OrdersRepository,
                                                private val userRepository: UserRepository,
                                                private val chatRepository: ChatRepository,
                                                private val cartRepository: CartRepository,
                                                private val authRepository: AuthRepository): MvpPresenter<SalesHistoryMvpView>() {

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
                        viewState.onSalesLoaded(inactiveSales, addresses, avatars, emptyList())
                    }
                    else {
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
                                    it.deliveryStatus = if(info.entity.statuses.first().code == CdekStatuses.INVALID) context.getString(
                                        R.string.delivery_data_incorrect)
                                    else  info.entity.statuses.first().name

                                }else it.deliveryStatus = context.getString(R.string.uknown_delivery_status)

                            }
                            if(it.idYandex != null && it.delivery == 2){
                                val info = ordersRepository.getYandexGoOrderInfo(it.idYandex)?.body()
                                if(info != null){
                                    it.deliveryStatus = if(info.status == "ready_for_approval") null else YandexDeliveryStatus.statuses[info.status]
                                }else it.deliveryStatus = context.getString(R.string.uknown_delivery_status)
                            }
                            it
                        }
                        viewState.onSalesLoaded(activeSalesSorted, addresses, avatars, dialogs)
                    }
                    val cartItems = getCartItems() ?: emptyList()
                    val orderItems = getOrderItems()?.filter{it.cart != null}
                    val activeOrders = orderItems?.filter { it.status == 0 } ?: emptyList()
                    val inactiveOrders = orderItems?.filter{it.status == 1}.also{ Log.d("Sales", "inactiveOrders = $it")} ?: emptyList()
                    val filteredOrderItems = activeOrders.filter { activeOrders.find { orderCheck -> orderCheck.id != it.id && orderCheck.cart?.first()?.id == it.cart?.first()?.id &&
                            orderCheck.id > it.id } == null &&
                            cartItems.find { cartItem -> cartItem.product.id == it.cart?.first()?.id } == null}
                    val activeSalesFiltered = activeSales.filter { activeSales.find { saleCheck ->
                        saleCheck.id != it.id && saleCheck.cart?.first()?.id == it.cart?.first()?.id &&
                                saleCheck.idUser == it.idUser && saleCheck.id > it.id } == null }
                    viewState.onItemsLoaded(cartItems, filteredOrderItems, activeSalesFiltered, inactiveOrders, inactiveSales)
                }
                404 -> {
                    viewState.onSalesLoaded(emptyList(), emptyList(), emptyList(), emptyList())
                    val cartItems = getCartItems() ?: emptyList()
                    val orderItems = getOrderItems()?.filter{it.cart != null}
                    val activeOrders = orderItems?.filter { it.status == 0 } ?: emptyList()
                    val inactiveOrders = orderItems?.filter{it.status == 1}.also{ Log.d("Sales", "inactiveOrders = $it")} ?: emptyList()
                    viewState.onItemsLoaded(cartItems, activeOrders, emptyList(), inactiveOrders, emptyList())
                }
                else -> viewState.onError(errorMessage(response))
            }
        }
    }

    private suspend fun getAddresses(): List<OfferedOrderPlace>{
        val response = ordersRepository.getOrderAddresses()
        return when(response?.code()){
            200 -> response.body()!!
            else -> emptyList()
        }
    }

    private suspend fun getUser(id: Long): User? {
        val response = userRepository.getUser(id)
        return when(response?.code()){
            200 -> response.body()!!
            else -> {
                viewState.onError(errorMessage(response))
                null
            }
        }
    }

    private suspend fun getDialogs(): List<DialogWrapper>{
        val response = chatRepository.getDialogs()
        return if(response?.code() == 200) response.body()!! else emptyList()
    }

    private suspend fun getCartItems(): List<CartItem>?{
        val response = cartRepository.getCartItems(authRepository.getUserId())
        return if(response?.code() == 200 ) response.body() else null

    }

    private suspend fun getOrderItems(): List<Order>?{
        val response = ordersRepository.getAllOrders()
        return if(response?.code() == 200 ) response.body() else null

    }
}