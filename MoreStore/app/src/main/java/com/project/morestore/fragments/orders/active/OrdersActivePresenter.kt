package com.project.morestore.fragments.orders.active

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.project.morestore.R
import com.project.morestore.adapters.cart.OrderClickListener
import com.project.morestore.adapters.cart.OrdersAdapter
import com.project.morestore.dialogs.YesNoDialog
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.models.*
import com.project.morestore.models.cart.CartItem

import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.repositories.*
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import java.util.*

class OrdersActivePresenter(val context: Context)
    : MvpPresenter<OrdersActiveView>() {

    private var adapter: OrdersAdapter? = null
    private val ordersRepository = OrdersRepository()
    private val userRepository = UserRepository(context)
    private val chatRepository = ChatRepository(context)
    private val authRepository = AuthRepository(context)
    private val geoRepository = GeoRepository()
    private val cartRepository = CartRepository()

    override fun attachView(view: OrdersActiveView) {
        super.attachView(view)

        initContent();
    }



    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    private fun initContent() {
        if(adapter == null){
            val conf = Bitmap.Config.ARGB_8888 // see other conf types
            Bitmap.createBitmap(100, 100, conf)
            presenterScope.launch {
                val orders = getAllOrders()?.reversed() ?: return@launch
                val orderAddresses = getOrderAddresses() ?: return@launch
                val cartItems = getCartItems() ?: return@launch
                val dialogs = getDialogs()
                val orderItems = orders.filter { it.cart != null && it.status == 0 }.sortedBy{order ->

                    val address = orderAddresses.find { order.id == it.idOrder }
                    when{
                        order.cart?.first()?.statusUser?.buy == null -> 2
                        order.cart.first().statusUser?.buy?.status == 2 -> 3
                        address?.type == OfferedPlaceType.APPLICATION.value && address.status == 0 -> 1
                        address?.status == 1 -> 1
                        else -> 2
                    }
                }
                    .map { order ->
                    val user = getSellerUser(order.cart!!.first().idUser!!)
                        val dialog = dialogs?.find { it.product?.id == order.cart.first().id }
                        val chatFunctionInfo = if(dialog != null && order.cart.first().statusUser?.buy != null)
                            ChatFunctionInfo(dialogId = dialog.dialog.id, suggest = order.cart.first().statusUser?.buy?.id )
                        else null
                    val address = orderAddresses.find { it.idOrder == order.id  }
                    val time = if(address != null) Calendar.getInstance()
                        .apply { timeInMillis = address.address.substringAfter(";").toLongOrNull() ?: 0 }
                    else null

                    val buySuggest = order.cart.first().statusUser?.buy
                    var deliveryInfo: String? = null
                    var status = when(buySuggest?.status) {
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
                        if(order.pay == 2 && buySuggest?.status != 2){
                            if(!order.isPayment && buySuggest?.status == 1)
                                status = OrderStatus.NOT_PAYED

                                if(buySuggest?.status == 0 || buySuggest?.status == null)
                                    status = OrderStatus.NOT_SUBMITTED
                                if(order.idCdek != null && order.delivery == 3 && order.isPayment){
                                    Log.d("mylog", "cdek")
                                    val info = ordersRepository.getCdekOrderInfo(order.idCdek)?.body()
                                    if (info?.entity?.statuses?.first()?.code == CdekStatuses.INVALID)
                                        status = OrderStatus.DELIVERY_STATUS_NOT_VALID
                                     else if(info?.entity?.statuses?.first() != null) {
                                        status = OrderStatus.DELIVERY_STATUS_ACCEPTED
                                        deliveryInfo = info.entity.statuses.first().name
                                    }
                                    else if(info == null)
                                        status = OrderStatus.DELIVERY_STATUS_NOT_DEFINED
                                    else status = OrderStatus.DELIVERY_STATUS_NOT_DEFINED
                                }
                                if(order.idYandex != null && order.delivery == 2 && order.isPayment){
                                    val info = ordersRepository.getYandexGoOrderInfo(order.idYandex)?.body()
                                    if(info != null){
                                        status = OrderStatus.DELIVERY_STATUS_ACCEPTED
                                        deliveryInfo = YandexDeliveryStatus.statuses[info.status]
                                    }else status = OrderStatus.DELIVERY_STATUS_NOT_DEFINED
                                }
                                if(order.idCdek == null && order.idYandex == null && order.isPayment){
                                    status = OrderStatus.MEETING_NOT_ACCEPTED
                                }

                        }

                        val discountedPrice = when{
                            order.cart.first().statusUser?.price?.status == 1 -> order.cart.first().statusUser?.price?.value?.toIntOrNull()
                            order.cart.first().statusUser?.sale?.status == 1 -> order.cart.first().statusUser?.sale?.value?.toIntOrNull()
                            else -> null

                        }


                    OrderItem(
                        id = order.id,
                        user = user,
                        photo = order.cart.first().photo.first().photo,
                        name = order.cart.first().name,
                        price = discountedPrice ?: order.cart.first().priceNew?.toInt() ?: 0,
                        deliveryDate = if(time == null || time.timeInMillis == 0L)"-" else
                            "${time.get(Calendar.DAY_OF_MONTH)}.${time.get(Calendar.MONTH) + 1}.${time.get(Calendar.YEAR)}",
                        deliveryInfo = when (order.delivery) {
                            OrderCreateFragment.TAKE_FROM_SELLER -> context.getString(R.string.take_from_seller)
                            OrderCreateFragment.YANDEX_GO -> context.getString(R.string.yandex)
                            OrderCreateFragment.ANOTHER_CITY -> context.getString(R.string.cdek_rus)
                            else -> ""
                        },
                        status = status,
                        newAddress = address?.address ?: order.placeAddress,
                        newTime = if(time != null) "${time.get(Calendar.HOUR_OF_DAY)}:${time.get(Calendar.MINUTE)}"
                            else null,
                        sellerId = order.cart.first().idUser!!,
                        productId = order.cart.first().id,
                        newAddressId = address?.id,
                        product = order.cart.first(),
                        chatFunctionInfo,
                        buyerId = order.idUser,
                        cdekYandexAddress = order.placeAddress,
                        deliveryStatusInfo = deliveryInfo,
                        yandexGoOrderId = order.idYandex

                                )

                }

                val filteredOrderItems = orderItems.filter { orderItems.find { orderCheck -> orderCheck.id != it.id && orderCheck.productId == it.productId &&
                        orderCheck.id > it.id } == null &&
                        cartItems.find { cartItem -> cartItem.product.id == it.productId  } == null
                        && orders.find { initialOrder -> initialOrder.id != it.id && initialOrder.cart?.first()?.id == it.productId &&
                        initialOrder.status == 1} == null}

                adapter = OrdersAdapter(filteredOrderItems, createOrderClickListener(), {user -> viewState.navigate(user) }, {
                    viewState.navigate(it)
                })

                viewState.initActiveOrders(adapter!!)
                viewState.loading(false)
            }
        }

    }

    private suspend fun getAllOrders(): List<Order>?{
        val response = ordersRepository.getAllOrders()
        return when(response?.code()){
            200 -> response.body()
            404 -> emptyList()
            else -> {
                viewState.showMessage(errorMessage(response))
                viewState.loading(false)
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
                viewState.loading(false)
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
                viewState.loading(false)
                null
            }
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
                    viewState.showMessage(context.getString(R.string.place_accepted))
                    adapter = null
                    initContent()
                }
                else -> {
                    viewState.showMessage(errorMessage(response))
                    viewState.loading(false)
                    null
                }
            }
        }
    }

    private fun submitReceiveOrder(orderId: Long){
        presenterScope.launch {
            val response = ordersRepository.submitReceiveOrder(OrderId(orderId))
            when(response?.code()){
                200 -> viewState.navigate(R.id.ordersHistoryFragment)
                else -> {
                    viewState.showMessage(errorMessage(response))
                    viewState.loading(false)
                    null
                }
            }

        }
    }

    private suspend fun getDialogs(): List<DialogWrapper>?{
        val response = chatRepository.getDialogs()
        return if(response?.code() == 200) response.body()!! else null
    }

    private suspend fun getCartItems(): List<CartItem>?{
        val response = cartRepository.getCartItems(authRepository.getUserId())
        return when(response?.code()){
            200 -> response.body()
            404 -> emptyList()
            else -> {
                viewState.showMessage(errorMessage(response))
                viewState.loading(false)
                null
            }
        }
    }

    private suspend fun getFinalYandexGoPrice(toAddress: String, product: Product, promo: String? = null): Float?{
        val fromCoords = geoRepository.getCoordsByAddress(product.address?.fullAddress!!)?.body()?.coords
            if(fromCoords == null){
                viewState.showMessage(context.getString(R.string.error_getting_price))
                viewState.showMessage("ошибка оценки стоимости")
                viewState.loading(false)
                return null
            }
            val toCoords = geoRepository.getCoordsByAddress(toAddress)?.body()?.coords
            if(toCoords == null){
                viewState.showMessage(context.getString(R.string.error_getting_price))
                viewState.showMessage("ошибка оценки стоимости")
                viewState.loading(false)
                return null
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
            val response = ordersRepository.getYandexGoPrice(info)
            return when(response?.code()){
                200 -> {
                    val discountedPrice = when{
                        product.statusUser?.price?.status == 1 -> product.statusUser.price.value
                        product.statusUser?.sale?.status == 1 -> product.statusUser.sale.value
                        else -> null
                    }
                    val promoInfo = if(promo != null) getPromoInfo(promo) else null
                    val priceWithDelivery = (discountedPrice?.toFloatOrNull() ?: product.priceNew ?: product.price) + response.body()?.price!!.toFloat()
                    val finalPrice = (priceWithDelivery + (priceWithDelivery * 0.05)) - (promoInfo?.sum ?: 0)
                    finalPrice.toFloat()
                }
                else -> {
                    viewState.showMessage(errorMessage(response))
                    viewState.loading(false)
                    null
                }
            }
    }

    private suspend fun getPromoInfo(code: String): PromoCode?{
        val response = ordersRepository.getPromoInfo(code)
        return when (response?.code()) {
            200 -> {
                response.body()
            }
            404 -> {
                viewState.showMessage("промокод не найден")
                viewState.loading(false)
                null
            }
            else -> {
                viewState.showMessage(errorMessage(response))
                viewState.loading(false)
                null
            }
        }

    }

    private fun getPaymentUrl(sum: Float, orderId: Long){
        presenterScope.launch {
            val response = ordersRepository.payForOrder(PayOrderInfo(sum, orderId))
            when(response?.code()){
                200 -> {
                    viewState.payment(response.body()!!, orderId)
                }
                else -> {
                    viewState.showMessage(errorMessage(response))
                    viewState.loading(false)
                }
            }
        }

    }

    private suspend fun getFinalCdekPrice(toAddress: String, product: Product, promo: String? = null): Float?{
        val dimensions = ProductDimensions(
                length = product.packageDimensions.length ?: "10",
                width = product.packageDimensions.width ?: "10",
                height = product.packageDimensions.height ?: "10",
                weight = ((product.packageDimensions.weight ?: "0.3").toFloat() * 1000).toInt().toString()
            )
            val isCdekPickupPoint = toAddress.contains("cdek code:")
            val tariff = if(isCdekPickupPoint) 136 else 137
            val info = CdekCalculatePriceInfo(
                tariff_code = tariff,
                from_location = AddressString(product.addressCdek?.substringBefore("cdek code") ?: ""),
                to_location = AddressString(toAddress.substringBefore("cdek code")),
                packages = dimensions
            )
            val response = ordersRepository.getCdekPrice(info)
            return when(response?.code()){
                200 -> {
                    val discountedPrice = when{
                        product.statusUser?.price?.status == 1 -> product.statusUser.price.value
                        product.statusUser?.sale?.status == 1 -> product.statusUser.sale.value
                        else -> null
                    }
                    val promoInfo = if(promo != null) getPromoInfo(promo) else null
                    val priceWithDelivery = (discountedPrice?.toFloatOrNull() ?: product.priceNew ?: product.price) + response.body()!!.total_sum
                    val finalPrice = (priceWithDelivery + (priceWithDelivery * 0.05)) - (promoInfo?.sum ?: 0)
                    finalPrice.toFloat()
                }
                else -> {
                    viewState.showMessage(errorMessage(response))
                    viewState.loading(false)
                    null
                }
            }

    }

    private fun createOrderClickListener(): OrderClickListener{
        return object : OrderClickListener {
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

            override fun payForOrder(orderItem: OrderItem) {
                presenterScope.launch {
                    viewState.loading()
                    val isYandex = orderItem.deliveryInfo == context.getString(R.string.yandex)
                    if (isYandex) {
                        val toAddress = orderItem.cdekYandexAddress
                        val finalPrice = getFinalYandexGoPrice(
                            toAddress = toAddress.orEmpty(),
                            product = orderItem.product,
                            promo = orderItem.promo)
                        finalPrice ?: return@launch
                        getPaymentUrl(sum = finalPrice, orderItem.id)
                    }else{
                        val toAddress = orderItem.cdekYandexAddress
                        val finalPrice = getFinalCdekPrice(
                            toAddress = toAddress.orEmpty(),
                            product = orderItem.product,
                            promo = orderItem.promo)
                        finalPrice ?: return@launch
                        getPaymentUrl(sum = finalPrice, orderId = orderItem.id)
                    }
                }

            }
        }

    }

}