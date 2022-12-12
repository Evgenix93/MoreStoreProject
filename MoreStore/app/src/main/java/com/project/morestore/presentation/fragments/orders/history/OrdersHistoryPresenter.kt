package com.project.morestore.presentation.fragments.orders.history

import android.content.Context
import com.project.morestore.R
import com.project.morestore.presentation.adapters.cart.OrdersHistoryAdapter
import com.project.morestore.presentation.fragments.orders.create.OrderCreateFragment
import com.project.morestore.data.models.OfferedOrderPlace
import com.project.morestore.data.models.Order
import com.project.morestore.data.models.User
import com.project.morestore.data.models.cart.OrderHistoryItem
import com.project.morestore.data.models.cart.OrderHistoryStatus
import com.project.morestore.data.repositories.OrdersRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.util.errorMessage
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import java.util.*
import javax.inject.Inject

class OrdersHistoryPresenter @Inject constructor(@ActivityContext private val context: Context,
                                                 private val ordersRepository: OrdersRepository,
                                                 private val userRepository: UserRepository)
    : MvpPresenter<OrdersHistoryView>() {

    private var adapter: OrdersHistoryAdapter? = null



    override fun attachView(view: OrdersHistoryView) {
        super.attachView(view)
        initContent();
    }

    private fun initContent() {
        if(adapter == null) {

                presenterScope.launch {
                    val orders = getCompletedOrders() ?: return@launch
                    val orderAddresses = getOrderAddresses() ?: return@launch

                    val orderItems = orders.filter { it.cart != null }.sortedBy{order ->
                        val timestamp = orderAddresses.find{address -> address.idOrder == order.id}
                            ?.address?.substringAfter(';')?.toLongOrNull()
                        if(timestamp != null)
                            (timestamp - System.currentTimeMillis())
                        else {
                            null
                        }
                    }
                        .map { order ->
                        val user = getSellerUser(order.cart!!.first().idUser!!)
                        val address = orderAddresses.find { it.idOrder == order.id && it.status == 1 }
                        val time = if (address != null) Calendar.getInstance()
                            .apply {
                                timeInMillis =
                                    address.address.substringAfter(";").toLongOrNull() ?: 0
                            }
                        else null

                            val discountedPrice = when{
                                order.cart.first().statusUser?.price?.status == 1 -> order.cart.first().statusUser?.price?.value?.toIntOrNull()
                                order.cart.first().statusUser?.sale?.status == 1 -> order.cart.first().statusUser?.sale?.value?.toIntOrNull()
                                else -> null

                            }


                        OrderHistoryItem(
                            id = order.id.toString(),
                            user = user,
                            photo = order.cart.first().photo.first().photo,
                            name = order.cart.first().name,
                            price = discountedPrice ?: order.cart.first().priceNew?.toInt() ?: 0,
                            deliveryDate = if (time == null || time.timeInMillis == 0L) "-" else
                                "${time.get(Calendar.DAY_OF_MONTH)}.${time.get(Calendar.MONTH) + 1}.${
                                    time.get(
                                        Calendar.YEAR
                                    )
                                }",
                            deliveryInfo = when (order.delivery) {
                                OrderCreateFragment.TAKE_FROM_SELLER -> context.getString(R.string.take_from_seller)
                                OrderCreateFragment.YANDEX_GO -> context.getString(R.string.yandex)
                                OrderCreateFragment.ANOTHER_CITY -> context.getString(R.string.cdek_rus)
                                else -> ""
                            },
                            status = OrderHistoryStatus.COMPLETED,
                            newAddress = address?.address,
                            newTime = if(time != null) "${time.get(Calendar.HOUR_OF_DAY)}:${time.get(Calendar.MINUTE)}"
                            else null,
                            sellerId = order.cart.first().idUser!!,
                            productId = order.cart.first().id,
                            newAddressId = address?.id,
                            product = order.cart.first()
                        )
                    }

                    adapter = OrdersHistoryAdapter(orderItems,{}, {user ->
                        viewState.navigate(user)
                    }, {
                        viewState.navigate(it)

                    })
                    viewState.initOrdersHistory(adapter!!)
                }
        }
    }

    private suspend fun getCompletedOrders(): List<Order>?{
        val response = ordersRepository.getAllOrders()
        return when(response?.code()){
            200 -> response.body()?.filter { it.status == 1 }
            404 -> emptyList()
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun getSellerUser(userId: Long): User?{
        val response = userRepository.getSellerInfo(userId)
        return when(response?.code()){
            200 -> response.body()
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun getOrderAddresses(): List<OfferedOrderPlace>?{
        val response = ordersRepository.getOrderAddresses()
        return when(response?.code()){
            200 -> response.body()
            404 -> emptyList()
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }
}