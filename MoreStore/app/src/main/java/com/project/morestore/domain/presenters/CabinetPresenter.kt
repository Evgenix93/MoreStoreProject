package com.project.morestore.domain.presenters

import android.util.Log
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.CartRepository
import com.project.morestore.data.repositories.OrdersRepository
import com.project.morestore.data.repositories.SalesRepository
import com.project.morestore.presentation.mvpviews.CabinetMvpView
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class CabinetPresenter @Inject constructor(private val salesRepository: SalesRepository, private val ordersRepository: OrdersRepository,
private val cartRepository: CartRepository, private val authRepository: AuthRepository) :
    MvpPresenter<CabinetMvpView>() {

    fun getSalesCount() {
        presenterScope.launch {
                val sales = salesRepository.getSales()?.body()?.reversed().orEmpty()
                val activeSales = sales.filter { it.status == 0 }
                val activeSalesFiltered = activeSales.filter {
                    activeSales.find { saleCheck ->
                        saleCheck.id != it.id && saleCheck.cart?.first()?.id == it.cart?.first()?.id &&
                                saleCheck.idUser == it.idUser && saleCheck.id > it.id } == null}
                val finishedSales = sales.filter{it.status == 1}
                viewState.showSalesCount(activeSalesFiltered.size, finishedSales.size)
            }
    }

    fun getOrdersCount(){
        presenterScope.launch {
                val orderItems = ordersRepository.getAllOrders()?.body().orEmpty()
                val activeOrders = orderItems.filter { it.status == 0 }
                val cartItems = cartRepository.getCartItems(authRepository.getUserId())?.body().orEmpty()
                val filteredOrderItems = activeOrders.filter { activeOrders.find { orderCheck -> orderCheck.id != it.id && orderCheck.cart?.first()?.id == it.cart?.first()?.id &&
                        orderCheck.id > it.id } == null &&
                        cartItems.find { cartItem -> cartItem.product.id == it.cart?.first()?.id } == null}
            val inactiveOrders = orderItems.filter{it.status == 1}
            viewState.showOrdersCount(filteredOrderItems.size, inactiveOrders.size)
            }
        }
}