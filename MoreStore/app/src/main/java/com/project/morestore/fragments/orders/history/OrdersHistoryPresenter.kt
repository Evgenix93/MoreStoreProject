package com.project.morestore.fragments.orders.history

import android.content.Context
import android.graphics.Bitmap
import com.project.morestore.adapters.cart.OrdersAdapter
import com.project.morestore.adapters.cart.OrdersHistoryAdapter
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.models.OfferedOrderPlace
import com.project.morestore.models.OfferedPlaceType
import com.project.morestore.models.Order
import com.project.morestore.models.User
import com.project.morestore.models.cart.OrderHistoryItem
import com.project.morestore.models.cart.OrderHistoryStatus
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import java.util.*

class OrdersHistoryPresenter(context: Context)
    : MvpPresenter<OrdersHistoryView>() {

    private var adapter: OrdersHistoryAdapter? = null
    private val ordersRepository = OrdersRepository(context)
    private val userRepository = UserRepository(context)


    override fun attachView(view: OrdersHistoryView) {
        super.attachView(view)

        initContent();
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    private fun initContent() {

        //if (!this::adapter.isInitialized) {
        if(adapter == null) {
            //val conf = Bitmap.Config.ARGB_8888 // see other conf types
            //val bmp = Bitmap.createBitmap(100, 100, conf)

           /* adapter = OrdersHistoryAdapter(
                listOf(
                    OrderHistoryItem(
                        "123123121",
                        bmp,
                        "Екатерина",
                        bmp,
                        "Кеды",
                        3000,
                        "19 февраля",
                        "Lorem ipsum",
                        OrderHistoryStatus.COMPLETED
                    ),
                    OrderHistoryItem(
                        "123123121",
                        bmp,
                        "Екатерина",
                        bmp,
                        "Кеды",
                        3000,
                        "19 февраля",
                        "Lorem ipsum",
                        OrderHistoryStatus.COMPLETED
                    ),
                    OrderHistoryItem(
                        "123123121",
                        bmp,
                        "Екатерина",
                        bmp,
                        "Кеды",
                        3000,
                        "19 февраля",
                        "Lorem ipsum",
                        OrderHistoryStatus.COMPLETED
                    )
                )
            ) {

            }*/

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


                        OrderHistoryItem(
                            id = order.id.toString(),
                            //userIcon = user?.avatar?.photo.toString(),
                            //userName = user?.name.orEmpty(),
                            user = user,
                            photo = order.cart.first().photo.first().photo,
                            name = order.cart.first().name,
                            price = order.cart.first().priceNew?.toInt() ?: 0,
                            deliveryDate = if (time == null || time.timeInMillis == 0L) "-" else
                                "${time.get(Calendar.DAY_OF_MONTH)}.${time.get(Calendar.MONTH) + 1}.${
                                    time.get(
                                        Calendar.YEAR
                                    )
                                }",
                            deliveryInfo = when (order.delivery) {
                                OrderCreateFragment.TAKE_FROM_SELLER -> "Заберу у продавца"
                                OrderCreateFragment.YANDEX_GO -> "yandex"
                                OrderCreateFragment.ANOTHER_CITY -> "СДЕК"
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


                    /*adapter = OrdersAdapter(
                listOf(
                    OrderItem(
                        123123121,
                        bmp,
                        "Екатерина",
                        bmp,
                        "Кеды",
                        100,
                        "18 апреля",
                        "CДЭK",
                        OrderStatus.RECEIVED,
                        null,
                        null
                    ),
                    OrderItem(
                        123123121,
                        bmp,
                        "Екатерина",
                        bmp,
                        "Кеды",
                        100,
                        "18 апреля",
                        "CДЭK",
                        OrderStatus.CHANGE_MEETING,
                        null,
                        null
                    ),
                    OrderItem(
                        123123121,
                        bmp,
                        "Екатерина",
                        bmp,
                        "Кеды",
                        100,
                        "18 апреля",
                        "CДЭK",
                        OrderStatus.AT_COURIER,
                        null,
                        null
                    )
                ),
                listener
            )*/



                    viewState.initOrdersHistory(adapter!!)
                }
        }
    }

    private suspend fun getCompletedOrders(): List<Order>?{
        val response = ordersRepository.getAllOrders()
        return when(response?.code()){
            200 -> response.body()?.filter { it.status == 1 }
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

    private suspend fun getSellerUser(userId: Long): User?{
        val response = userRepository.getSellerInfo(userId.toInt())
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

    private suspend fun getOrderAddresses(): List<OfferedOrderPlace>?{
        val response = ordersRepository.getOrderAddresses()
        return when(response?.code()){
            200 -> response.body()
            null -> {
                viewState.showMessage("Нет интернета")
                null
            }
            500 -> {
                viewState.showMessage("500 Internal Server Error")
                null
            }
            400 -> {
                viewState.showMessage(response.errorBody()?.string().orEmpty())
                null
            }
            404 -> emptyList()
            else -> null

        }
    }
}