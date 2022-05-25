package com.project.morestore.presenters

import android.content.Context
import androidx.core.view.isVisible
import com.project.morestore.R
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.models.*
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.mvpviews.OrderDetailsView
import com.project.morestore.repositories.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import java.util.*

class OrderDetailsPresenter(context: Context): MvpPresenter<OrderDetailsView>()  {
    private val ordersRepository = OrdersRepository(context)
    private val chatRepository = ChatRepository(context)
    private val authRepository = AuthRepository(context)
    private val salesRepository = SalesRepository()
    private val productRepository = ProductRepository(context)
    private val userRepository = UserRepository(context)

     fun acceptOrderPlace(orderId: Long, addressId: Long, address: String, asBuyer: Boolean) {
         presenterScope.launch {
             viewState.loading(true)
             val change = OfferedOrderPlaceChange(
                 idOrder = orderId,
                 idAddress = addressId,
                 address = address,
                 status = 1
             )
             val response = ordersRepository.changeOrderPlaceStatus(change)
             when (response?.code()) {
                 200 -> {
                     viewState.loading(false)
                     viewState.showMessage("Место сделки принято")
                     viewState.orderStatusChanged(if (asBuyer) OrderStatus.RECEIVED else OrderStatus.RECEIVED_SELLER)

                 }
                 null -> {
                     viewState.loading(false)
                     viewState.showMessage("нет интернета")

                 }
                 400 -> {
                     viewState.loading(false)
                     viewState.showMessage(response.errorBody()!!.string())

                 }
                 500 -> {
                     viewState.loading(false)
                     viewState.showMessage("500 Internal Server Error")

                 }
                 else -> {
                     viewState.loading(false)
                 }
             }
         }
     }




    fun addDealPlace(orderId: Long, address: String){
        presenterScope.launch{
            val response = salesRepository.addDealPlace(orderId, address)
            when(response?.code()){
                200 -> {

                viewState.loading(false)
                    if(response.body()!!)
                        viewState.orderStatusChanged(OrderStatus.MEETING_NOT_ACCEPTED_SELLER)
                    else
                        viewState.showMessage("Ошибка при добавлении адреса")
                }
                400 -> {
                    viewState.showMessage(getError(response.errorBody()!!))
                    viewState.loading(false)
                }
                null -> {
                    viewState.showMessage("Нет интернета")
                    viewState.loading(false)
                }
            }
        }
    }

    /*fun acceptOrderPlace(offeredOrderPlaceChange: OfferedOrderPlaceChange){
        presenterScope.launch {
            val response = ordersRepository.changeOrderPlaceStatus(offeredOrderPlaceChange)
            when(response?.code()){
                200 -> {
                    viewState.orderStatusChanged(OrderStatus.RECEIVED_SELLER)
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
>>>>>>> origin/main

            }
        }
    }*/


     fun submitReceiveOrder(orderId: Long){
        presenterScope.launch {
            val response = ordersRepository.submitReceiveOrder(OrderId(orderId))
            when(response?.code()){
                200 -> {
                    viewState.loading(false)
                    viewState.orderStatusChanged(OrderStatus.RECEIVED_SUCCESSFULLY)
                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")

                }
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()!!.string())

                }
                500 -> {
                    viewState.loading(false)
                    viewState.showMessage("500 Internal Server Error")

                }
                else -> {viewState.loading(false)}

            }

        }
    }

    fun cancelBuyRequest(orderItem: OrderItem) {
        presenterScope.launch {
            viewState.loading(true)
            val response = chatRepository.cancelBuyRequest(orderItem.chatFunctionInfo!!)
            when (response?.code()) {
                200 -> {
                    viewState.loading(false)
                    viewState.orderStatusChanged(if (authRepository.getUserId() == orderItem.sellerId) OrderStatus.DECLINED else OrderStatus.DECLINED_BUYER)
                }
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()?.string().orEmpty())
                }
                500 -> {
                    viewState.loading(false)
                    viewState.showMessage("500 Internal Server Error")
                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")
                }
                404 -> {
                    viewState.loading(false)
                    viewState.showMessage("ошибка 404 not found")
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage("ошибка")
                }
            }
        }
    }

    fun submitBuy(orderItem: OrderItem){
        presenterScope.launch {
            val response = chatRepository.submitBuy(orderItem.chatFunctionInfo!!)
            when(response?.code()){
                200 -> {
                   when {
                       orderItem.newAddress == null -> viewState.orderStatusChanged(OrderStatus.ADD_MEETING)
                       orderItem.offeredOrderPlace?.type == OfferedPlaceType.PROPOSED.value -> viewState.orderStatusChanged(OrderStatus.MEETING_NOT_ACCEPTED_SELLER)
                       orderItem.offeredOrderPlace?.type == OfferedPlaceType.APPLICATION.value -> viewState.orderStatusChanged(OrderStatus.CHANGE_MEETING_SELLER)
                   }
                }
                400 -> {
                    val bodyString = getError(response.errorBody()!!)
                    viewState.showMessage(bodyString)
                }
                500 -> viewState.showMessage("500 Internal Server Error")
                null -> viewState.showMessage("Ошибка")
            }
        }
    }

    fun getProductById(productId: Long){
        presenterScope.launch {
            viewState.loading(true)
            val response = productRepository.getProducts(productId = productId)
            when (response?.code()) {
                200 -> {
                    viewState.loading(false)
                    viewState.productLoaded(response.body()?.first()!!)
                }
                400 -> {
                    viewState.loading(false)
                    viewState.showMessage(response.errorBody()?.string().orEmpty())
                }
                500 -> {
                    viewState.loading(false)
                    viewState.showMessage("500 Internal Server Error")
                }
                null -> {
                    viewState.loading(false)
                    viewState.showMessage("нет интернета")
                }
                404 -> {
                    viewState.loading(false)
                    viewState.showMessage("ошибка 404 not found")
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage("ошибка")
                }
            }

        }
    }

    fun getOrderItem(orderId: Long){
        presenterScope.launch {
            viewState.loading(true)
            val orders = getAllOrders()
            var order = orders?.find { it.id == orderId }
            if(order == null){
                val sales = getAllSales()
                order = sales?.find { it.id == orderId }
            }
            order ?: return@launch
            val isBuyer = order.idUser == authRepository.getUserId()
            val dialog = getAllDialogs()?.find { it.product?.id == order.cart?.first()?.id }
            val discountedPrice = when{
                order.cart?.first()?.statusUser?.price?.status == 1 -> order.cart?.first()?.statusUser?.price?.value?.toIntOrNull()
                order.cart?.first()?.statusUser?.sale?.status == 1 -> order.cart?.first()?.statusUser?.sale?.value?.toIntOrNull()
                else -> null

            }
            val chatFunctionInfo = if( dialog != null && order.cart?.first()?.statusUser?.buy?.status != 2)
                ChatFunctionInfo(dialogId = dialog.dialog.id, suggest = order.cart?.first()?.statusUser?.buy?.id,
                    value = discountedPrice ?: order.cart?.first()?.priceNew?.toInt() )
            else null
            val user = if(isBuyer) getUserById(order.idSeller ?: -1) else getUserById(order.idUser ?: -1)
            val address = getOfferedAddresses()?.find { it.idOrder == orderId }
            val time = if(address != null) Calendar.getInstance()
                .apply { timeInMillis = address.address.substringAfter(";").toLongOrNull() ?: 0 }
            else null
            val buySuggest = order.cart?.first()?.statusUser?.buy
            var status: OrderStatus = OrderStatus.MEETING_NOT_ACCEPTED
            if(isBuyer)
            status = when(buySuggest?.status) {
                0 -> OrderStatus.NOT_SUBMITTED
                1 -> {
                    if (address == null) OrderStatus.MEETING_NOT_ACCEPTED
                    else when {
                        address.type == OfferedPlaceType.APPLICATION.value
                                && address.status == 0 -> OrderStatus.CHANGE_MEETING
                        address.status == 1 -> OrderStatus.RECEIVED
                        address.type == OfferedPlaceType.PROPOSED.value
                                && address.status == 0 -> OrderStatus.CHANGE_MEETING_FROM_ME
                        else -> OrderStatus.MEETING_NOT_ACCEPTED
                    }
                }
                2 -> {
                    if(buySuggest.idCanceled == authRepository.getUserId() )
                        OrderStatus.DECLINED_BUYER
                    else OrderStatus.DECLINED
                }
                else -> OrderStatus.NOT_SUBMITTED
            }
            else
                when (address?.type) {
                    OfferedPlaceType.PROPOSED.value -> {

                        if (address.status == 0) {
                            status = OrderStatus.MEETING_NOT_ACCEPTED_SELLER

                        }
                        else {
                            status = OrderStatus.RECEIVED_SELLER

                        }
                    }
                    OfferedPlaceType.APPLICATION.value -> {
                        if (address.status == 0) {
                            status = OrderStatus.CHANGE_MEETING_SELLER


                        } else {
                            status = OrderStatus.RECEIVED_SELLER

                        }
                    }
                }

            if(order.status == 1) status = OrderStatus.RECEIVED_SUCCESSFULLY



            val orderItem = OrderItem(
                id = order.id,
                //userIcon = user?.avatar?.photo.toString(),
                //userName = user?.name.orEmpty(),
                user = user,
                photo = order.cart?.first()?.photo?.first()?.photo!!,
                name = order.cart?.first()?.name.orEmpty(),
                price = discountedPrice ?: order.cart?.first()?.priceNew?.toInt() ?: 0,
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
                sellerId = order.cart?.first()?.idUser!!,
                productId = order.cart?.first()?.id!!,
                newAddressId = address?.id,
                product = order.cart?.first()!!,
                chatFunctionInfo

            )

            viewState.orderItemLoaded(orderItem)








        }
    }

    fun initProfile(order: OrderItem){
        presenterScope.launch {
            val currentUserId = authRepository.getUserId()
            if (currentUserId == order.sellerId) {
                val user = getUserById(order.user?.id ?: -1)
                viewState.setProfileInfo(user?.avatar?.photo.toString(), user?.name.orEmpty())
            }else{
                val user = getUserById(order.sellerId)
                viewState.setProfileInfo(user?.avatar?.photo.toString(), user?.name.orEmpty())
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

    private suspend fun getAllSales(): List<Order>?{
        val response = salesRepository.getSales()
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

    private suspend fun getAllDialogs(): List<DialogWrapper>?{
        val response = chatRepository.getDialogs()
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

    private suspend fun getUserById(id: Long): User?{
        val response = userRepository.getUser(id)
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
            404 -> null
            else -> null

        }


    }

    private suspend fun getOfferedAddresses(): List<OfferedOrderPlace>?{
        val response = ordersRepository.getOrderAddresses()
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

    /*fun cancelBuyRequest(info: ChatFunctionInfo){
        presenterScope.launch {
            val response = chatRepository.cancelBuyRequest(info)
            when (response?.code()) {
                200 -> {
                    viewState.orderStatusChanged(OrderStatus.DECLINED)
                }
                400 -> {
                    val bodyString = getError(response.errorBody()!!)
                    viewState.onError(bodyString)
                }
                500 -> viewState.onError("500 Internal Server Error")
                null -> viewState.onError("нет интернета")
                404 -> viewState.onError("ошибка 404 not found")
                else -> viewState.onError("ошибка")
>>>>>>> origin/main

            }

        }
    }*/



    private suspend fun getError(errorBody: ResponseBody): String{
        return  withContext(Dispatchers.IO){
            errorBody.string()
        }
    }

}