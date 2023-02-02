package com.project.morestore.domain.presenters

import android.content.Context
import com.project.morestore.R
import com.project.morestore.presentation.fragments.orders.create.OrderCreateFragment
import com.project.morestore.data.models.*
import com.project.morestore.data.models.cart.OrderItem
import com.project.morestore.data.models.cart.OrderStatus
import com.project.morestore.presentation.mvpviews.OrderDetailsView
import com.project.morestore.data.repositories.*
import com.project.morestore.util.errorMessage
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import java.util.*
import javax.inject.Inject

class OrderDetailsPresenter @Inject constructor(
    @ActivityContext private val context: Context,
    private val ordersRepository: OrdersRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val salesRepository: SalesRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val geoRepository: GeoRepository
): MvpPresenter<OrderDetailsView>()  {

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
                     viewState.showMessage(context.getString(R.string.deal_place_accepted))
                     viewState.orderStatusChanged(if (asBuyer) OrderStatus.RECEIVED else OrderStatus.RECEIVED_SELLER)

                 }
                 else -> {
                     viewState.loading(false)
                     viewState.showMessage(errorMessage(response))
                 }
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

     fun submitReceiveOrder(orderId: Long){
        presenterScope.launch {
            val response = ordersRepository.submitReceiveOrder(OrderId(orderId))
            when(response?.code()){
                200 -> {
                    viewState.loading(false)
                    viewState.orderStatusChanged(OrderStatus.RECEIVED_SUCCESSFULLY)
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
                }
            }
        }
    }

    fun cancelBuyRequest(orderItem: OrderItem) {
        presenterScope.launch {
            viewState.loading(true)
            val response = chatRepository.cancelBuyRequest(orderItem.chatFunctionInfo!!)
            when (response?.code()) {
                200 -> {
                    if(orderItem.yandexGoOrderId != null)
                        cancelYandexGoOrder(orderItem.yandexGoOrderId)
                    if(orderItem.sberId != null)
                    ordersRepository.returnMoney(ReturnMoneyInfo(orderItem.sberId, orderItem.sum))
                    viewState.loading(false)
                    viewState.orderStatusChanged(if (authRepository.getUserId() == orderItem.sellerId) OrderStatus.DECLINED else OrderStatus.DECLINED_BUYER)
                }
                404 -> {
                    viewState.loading(false)
                    viewState.showMessage("ошибка 404 not found")
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
                }
            }
        }
    }

    fun submitBuy(orderItem: OrderItem){
        presenterScope.launch {
            val response = chatRepository.submitBuy(orderItem.chatFunctionInfo!!)
            when(response?.code()){
                200 -> {
                    getOrderItem(orderItem.id)
                }
                else -> viewState.showMessage(errorMessage(response))
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

                404 -> {
                    viewState.loading(false)
                    viewState.showMessage("ошибка 404 not found")
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
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
                order.cart?.first()?.statusUser?.price?.status == 1 -> order.cart?.first()?.statusUser?.price?.value?.toFloatOrNull()
                order.cart?.first()?.statusUser?.sale?.status == 1 -> order.cart?.first()?.statusUser?.sale?.value?.toFloatOrNull()
                else -> null

            }
            val chatFunctionInfo = if( dialog != null && order.cart?.first()?.statusUser?.buy?.status != 2)
                ChatFunctionInfo(dialogId = dialog.dialog.id, suggest = order.cart?.first()?.statusUser?.buy?.id,
                    value = discountedPrice ?: order.cart?.first()?.priceNew )
            else null
            val user = if(isBuyer) getUserById(order.idSeller ?: -1) else getUserById(order.idUser ?: -1)
            val address = getOfferedAddresses()?.find { it.idOrder == orderId }
            val time = if(address != null) Calendar.getInstance()
                .apply { timeInMillis = address.address.substringAfter(";").toLongOrNull() ?: 0 }
            else null
            val buySuggest = order.cart?.first()?.statusUser?.buy
            var status: OrderStatus = OrderStatus.MEETING_NOT_ACCEPTED
            var info: CdekOrderInfo? = null
            var deliveryInfo: String? = null
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
                    null -> status = OrderStatus.ADD_MEETING
                }

            if(order.status == 1) status = OrderStatus.RECEIVED_SUCCESSFULLY

            if(order.pay == 2 && buySuggest?.status != 2){
                if(!order.isPayment) {
                    if(isBuyer && buySuggest?.status == 1)
                       status = OrderStatus.NOT_PAYED
                    if(isBuyer && (buySuggest?.status == 0 || buySuggest?.status == null))
                        status = OrderStatus.NOT_SUBMITTED
                    if(!isBuyer && (buySuggest?.status == 0 || buySuggest?.status == null))
                        status = OrderStatus.NOT_SUBMITTED_SELLER
                    if(!isBuyer && buySuggest?.status == 1)
                        status = OrderStatus.NOT_PAYED_SELLER
                }
                else{
                    if(order.idCdek == null && order.idYandex == null && buySuggest?.status == 1 && isBuyer)
                        status = OrderStatus.MEETING_NOT_ACCEPTED
                    if(order.idCdek == null && order.idYandex == null && buySuggest?.status == 1 && !isBuyer)
                        status = OrderStatus.CREATE_DELIVERY
                    if((buySuggest?.status == 0 || buySuggest?.status == null) && isBuyer)
                        status = OrderStatus.NOT_SUBMITTED
                    if((buySuggest?.status == 0 || buySuggest?.status == null) && !isBuyer)
                        status = OrderStatus.NOT_SUBMITTED_SELLER

                    if(order.idCdek != null && order.delivery == 3 && order.isPayment){
                        info = ordersRepository.getCdekOrderInfo(order.idCdek!!)?.body()
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
                        val infoYandex = ordersRepository.getYandexGoOrderInfo(order.idYandex!!)?.body()
                        if(infoYandex != null){
                            status = OrderStatus.DELIVERY_STATUS_ACCEPTED
                            deliveryInfo = YandexDeliveryStatus.statuses[infoYandex.status]
                        }else status = OrderStatus.DELIVERY_STATUS_NOT_DEFINED
                    }

                }
            }



            val orderItem = OrderItem(
                id = order.id,
                user = user,
                photo = order.cart?.first()?.photo?.first()?.photo!!,
                name = order.cart?.first()?.name.orEmpty(),
                price = discountedPrice ?: order.cart?.first()?.priceNew ?: 0f,
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
                sellerId = order.cart?.first()?.idUser!!,
                productId = order.cart?.first()?.id!!,
                newAddressId = address?.id,
                product = order.cart?.first()!!,
                chatFunctionInfo,
                cdekYandexAddress = order.placeAddress,
                yandexGoOrderId = order.idYandex,
                deliveryStatusInfo = deliveryInfo,
                cdekInfoEntity = info,
                sberId = order.sberId,
                sum = order.sum
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
            200 -> {
                viewState.loading(false)
                response.body()
            }
            404 -> {
                viewState.loading(false)
                emptyList()
            }
            else -> {
                viewState.loading(false)
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun getAllSales(): List<Order>?{
        val response = salesRepository.getSales()
        return when(response?.code()){
            200 -> response.body()
            404 -> emptyList()
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun getAllDialogs(): List<DialogWrapper>?{
        val response = chatRepository.getDialogs()
        return when(response?.code()){
            200 -> response.body()
            404 -> emptyList()
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun getUserById(id: Long): User?{
        val response = userRepository.getUser(id)
        return when(response?.code()){
            200 -> response.body()
            404 -> null
            else -> {
                viewState.showMessage(errorMessage(response))
                null
            }
        }
    }

    private suspend fun getOfferedAddresses(): List<OfferedOrderPlace>?{
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

    fun getPaymentUrl(sum: Float, orderId: Long, sellerPayout: Float){
        presenterScope.launch {
            viewState.loading(true)
            val response = ordersRepository.payForOrder(PayOrderInfo(sum, orderId, sellerPayout))
            when(response?.code()){
                200 -> {
                    viewState.loading(false)
                    viewState.payment(response.body()!!, orderId)
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
                }
            }
        }

    }

    fun getFinalCdekPrice(toAddress: String, product: Product, promo: String? = null){
        presenterScope.launch {
            viewState.loading(true)
            val dimensions = ProductDimensions(
                length = product.packageDimensions.length ?: "10",
                width = product.packageDimensions.width ?: "10",
                height = product.packageDimensions.height ?: "10",
                weight = ((product.packageDimensions.weight ?: "0.3").toFloat() * 1000).toInt().toString()
            )
            val isCdekPickupPoint = toAddress.contains("cdek code:")
            val isCdekPickupPostamat = toAddress.contains("cdek code postamat:")
            val tariff = if(isCdekPickupPoint) 136 else if(isCdekPickupPostamat) 368 else 137
            val info = CdekCalculatePriceInfo(
                tariff_code = tariff,
                from_location = AddressString(product.addressCdek?.substringBefore("cdek code") ?: ""),
                to_location = AddressString(toAddress.substringBefore(if(isCdekPickupPoint)"cdek code" else "cdek code postamat:")),
                packages = dimensions
            )
            val response = ordersRepository.getCdekPrice(info)
            when(response?.code()){
                200 -> {
                    val discountedPrice = when{
                        product.statusUser?.price?.status == 1 -> product.statusUser.price.value
                        product.statusUser?.sale?.status == 1 -> product.statusUser.sale.value
                        else -> null
                    }
                    val promoInfo = if(promo != null) getPromoInfo(promo) else null
                    val priceWithDelivery = (discountedPrice?.toFloatOrNull() ?: product.priceNew ?: product.price) + response.body()!!.total_sum
                    val finalPrice = (priceWithDelivery + (priceWithDelivery * 0.05)) - (promoInfo?.sum ?: 0)
                    viewState.setFinalPrice(finalPrice.toFloat())
                    viewState.loading(false)
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
                }
            }
        }
    }

    fun getFinalYandexGoPrice(toAddress: String, product: Product, promo: String? = null){
        presenterScope.launch {
            viewState.loading(true)
            val fromCoords = geoRepository.getCoordsByAddress(product.address?.fullAddress!!)?.body()?.coords
            if(fromCoords == null){
                viewState.loading(false)
                viewState.showMessage(context.getString(R.string.error_getting_price))
                return@launch
            }
            val toCoords = geoRepository.getCoordsByAddress(toAddress)?.body()?.coords
            if(toCoords == null){
                viewState.loading(false)
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
            val response = ordersRepository.getYandexGoPrice(info)
            when(response?.code()){
                200 -> {
                    val discountedPrice = when{
                        product.statusUser?.price?.status == 1 -> product.statusUser.price.value
                        product.statusUser?.sale?.status == 1 -> product.statusUser.sale.value
                        else -> null
                    }
                    val promoInfo = if(promo != null) getPromoInfo(promo) else null
                    val priceWithDelivery = (discountedPrice?.toFloatOrNull() ?: product.priceNew ?: product.price) + response.body()?.price!!.toFloat()
                    val finalPrice = (priceWithDelivery + (priceWithDelivery * 0.05)) - (promoInfo?.sum ?: 0)
                    viewState.setFinalPrice(finalPrice.toFloat())
                    viewState.loading(false)
                }
                else -> {
                    viewState.loading(false)
                    viewState.showMessage(errorMessage(response))
                }
            }
        }
    }

    fun getOrderForDelivery(orderId: Long){
        presenterScope.launch {
            viewState.loading(true)
            val order = getAllSales()?.find { it.id == orderId } ?: return@launch
            viewState.navigateToCreateDelivery(order)
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
                    null

                }

                else -> {
                    viewState.showMessage(errorMessage(response))
                    null
                }
            }

    }

    private suspend fun cancelYandexGoOrder(claimId: String): Boolean{
        val response = ordersRepository.cancelYandexGoOrder(YandexCancelClaimId(claimId = claimId))
        return when (response?.code()) {
            200 -> {
                response.body()?.code == null
            }

            404 -> {
                false
            }
            else -> {
                viewState.showMessage(errorMessage(response))
                false
            }
        }
    }
}