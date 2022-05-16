package com.project.morestore.fragments.orders.active

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.adapters.cart.OrderClickListener
import com.project.morestore.adapters.cart.OrdersAdapter
import com.project.morestore.dialogs.YesNoDialog
import com.project.morestore.fragments.ChatFragment
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.models.*

import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import java.util.*

class OrdersActivePresenter(val context: Context)
    : MvpPresenter<OrdersActiveView>() {

    private var adapter: OrdersAdapter? = null
    private val ordersRepository = OrdersRepository(context)
    private val userRepository = UserRepository(context)

    override fun attachView(view: OrdersActiveView) {
        super.attachView(view)

        initContent();
    }



    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    private fun initContent() {

        //if (!this::adapter.isInitialized) {
        if(adapter == null){

            val conf = Bitmap.Config.ARGB_8888 // see other conf types
            val bmp = Bitmap.createBitmap(100, 100, conf)

            val listener = object : OrderClickListener {
                override fun acceptMeeting(orderItem: OrderItem) {
                    acceptOrderPlace(orderItem.id, orderItem.newAddressId!!, orderItem.newAddress.orEmpty())

                }
                override fun declineMeeting(orderItem: OrderItem) {
                    viewState.navigateToChat(orderItem.sellerId, orderItem.productId)

                }

                override fun acceptOrder(orderItem: OrderItem) {
                    val acceptDialog = YesNoDialog(
                        context.getString(R.string.active_order_accept_dialog_title),
                        null,
                        object : YesNoDialog.onClickListener {
                            override fun onYesClick() {
                                submitReceiveOrder(orderItem.id)
                            }

                            override fun onNoClick() {

                            }

                        })

                    viewState.showAcceptOrderDialog(acceptDialog)
                }

                override fun reportProblem(orderItem: OrderItem) {
                    viewState.navigate(orderItem.id)
                }
            }
            presenterScope.launch {
                val orders = getAllOrders() ?: return@launch
                val orderAddresses = getOrderAddresses() ?: return@launch

                val orderItems = orders.filter { it.cart != null && it.status == 0 }.sortedBy{order ->
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
                    val address = orderAddresses.find { it.idOrder == order.id  }
                    val time = if(address != null) Calendar.getInstance()
                        .apply { timeInMillis = address.address.substringAfter(";").toLongOrNull() ?: 0 }
                    else null
                    val status = if(address == null) OrderStatus.MEETING_NOT_ACCEPTED
                    else when{
                        address.type == OfferedPlaceType.APPLICATION.value
                                && address.status == 0 -> OrderStatus.CHANGE_MEETING
                        address.status == 1 -> OrderStatus.RECEIVED
                        address.type == OfferedPlaceType.PROPOSED.value
                                && address.status == 0 -> OrderStatus.CHANGE_MEETING_FROM_ME
                        else -> OrderStatus.MEETING_NOT_ACCEPTED
                    }

                    OrderItem(
                        id = order.id,
                        userIcon = user?.avatar?.photo.toString(),
                        userName = user?.name.orEmpty(),
                        photo = order.cart.first().photo.first().photo,
                        name = order.cart.first().name,
                        price = order.cart.first().priceNew?.toInt() ?: 0,
                        deliveryDate = if(time == null || time.timeInMillis == 0L)"-" else
                            "${time.get(Calendar.DAY_OF_MONTH)}.${time.get(Calendar.MONTH) + 1}.${time.get(Calendar.YEAR)}",
                        deliveryInfo = when (order.delivery) {
                            OrderCreateFragment.TAKE_FROM_SELLER -> "Заберу у продавца"
                            OrderCreateFragment.YANDEX_GO -> "yandex"
                            OrderCreateFragment.ANOTHER_CITY -> "СДЕК"
                            else -> ""
                        },
                        status = status,
                        newAddress = address?.address,
                        newTime = if(time != null) "${time.get(Calendar.HOUR_OF_DAY)}:${time.get(Calendar.MINUTE)}"
                            else null,
                        sellerId = order.cart.first().idUser!!,
                        productId = order.cart.first().id,
                        newAddressId = address?.id

                                )

                }

                adapter = OrdersAdapter(orderItems, listener)


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
                viewState.initActiveOrders(adapter!!)
            }
        }

    }

    private suspend fun getAllOrders(): List<Order>?{
        val response = ordersRepository.getAllOrders()
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

    private fun acceptOrderPlace(orderId: Long, addressId: Long, address: String){
        presenterScope.launch {
            val change = OfferedOrderPlaceChange(
                idOrder = orderId,
                idAddress = addressId,
            address = address,
            status = 1)
            val response = ordersRepository.changeOrderPlaceStatus(change)
            when(response?.code()){
                200 -> {
                    viewState.showMessage("Место сделки принято")
                    adapter = null
                    initContent()
                }
                null -> {
                    viewState.showMessage("нет интернета")

                }
                400 -> {
                    viewState.showMessage(response.errorBody()!!.string())

                }
                500 -> {
                    viewState.showMessage("500 Internal Server Error")

                }
                else -> {}

            }
        }
    }

    private fun submitReceiveOrder(orderId: Long){
        presenterScope.launch {
            val response = ordersRepository.submitReceiveOrder(OrderId(orderId))
            when(response?.code()){
                200 -> viewState.navigate(R.id.ordersHistoryFragment)
                null -> {
                    viewState.showMessage("нет интернета")

                }
                400 -> {
                    viewState.showMessage(response.errorBody()!!.string())

                }
                500 -> {
                    viewState.showMessage("500 Internal Server Error")

                }
                else -> {}

            }

        }
    }

    /*private suspend fun initAdapter(): OrdersAdapter{
        val orders = getAllOrders()
        if(orders == null){
            adapter = OrdersAdapter(emptyList(), listener)
            return@launch
        }

        val orderItems = orders.filter { it.cart != null }.map { order ->
            OrderItem(
                order.id,
                Glide.with(context)
                    .load(order.cart.first().photo.first().photo)
                    .submit()
                    .get()
                    .toBitmap()
            )


        }


    }*/
}