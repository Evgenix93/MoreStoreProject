package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Log
import android.util.Range
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOrderDetailsBinding
import com.project.morestore.presentation.dialogs.DeleteDialog
import com.project.morestore.presentation.dialogs.YesNoDialog
import com.project.morestore.data.models.*
import com.project.morestore.data.models.cart.OrderItem
import com.project.morestore.data.models.cart.OrderStatus
import com.project.morestore.presentation.mvpviews.OrderDetailsView
import com.project.morestore.domain.presenters.OrderDetailsPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsFragment: MvpAppCompatFragment(R.layout.fragment_order_details), OrderDetailsView {
    private val binding: FragmentOrderDetailsBinding by viewBinding()
    private val args: OrderDetailsFragmentArgs by navArgs()
    private var orderStatus: OrderStatus? = null
    private lateinit var orderItem: OrderItem
    @Inject
    lateinit var orderDetailsPresenter: OrderDetailsPresenter
    private val presenter by moxyPresenter { orderDetailsPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        if(args.orderItem != null) {
            bind(args.orderItem!!)
            presenter.getProductById(args.orderItem!!.productId)
        }
        else presenter.getOrderItem(args.orderId)
        getSupportDialog()
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Статус заказа"
        binding.toolbar.backIcon.setOnClickListener {
        if(findNavController().previousBackStackEntry?.destination?.id == R.id.successOrderPaymentFragment
            || findNavController().previousBackStackEntry?.destination?.id == R.id.createOrderFragment)
            findNavController().navigate(R.id.ordersActiveFragment)
            else
            findNavController().popBackStack() }
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_support_black)
    }


    private fun bind(order: OrderItem) {
        Log.d("mylog", "orderStatus ${order.status}")
        Log.d("mylog", "order address ${order.cdekYandexAddress}")
        orderItem = order
        getDeliveryPrice(order)
        binding.allBlocks.isVisible = true
        orderStatus = order.status
        setAddress(order)
        presenter.initProfile(order)
        binding.chosenDeliveryTypeTextView.text = order.deliveryInfo
        setStatusInfo(order)
        setOrderStatus(order)
        binding.sellerAvatarImageView.setOnClickListener {
            if (order.user != null)
                findNavController().navigate(
                    OrderDetailsFragmentDirections.actionOrderDetailsFragmentToSellerProfileFragment(
                        user = order.user,
                        toReviews = false
                    )
                )

        }
        binding.sellerNameTextView.setOnClickListener {
            if (order.user != null)
                findNavController().navigate(
                    OrderDetailsFragmentDirections.actionOrderDetailsFragmentToSellerProfileFragment(
                      user =  order.user,
                       toReviews = false
                    )
                )

        }

        binding.cancelTextView.setOnClickListener {
            DeleteDialog(context = requireContext(),
                title = "Отменить сделку?",
                message = "Товар будет снова доступен для покупки другими покупателями.",
                confirmText = "Да, отменить",
                cancelText = "Нет",
                confirmCallback = {
                    if (order.chatFunctionInfo != null) presenter.cancelBuyRequest(order)
                    else showMessage("Диалог или заявка на покупку не найдены")
                }
            ).show()
        }
    }

    private fun getSupportDialog(){
        presenter.getSupportDialog()
    }

    private fun setProductInfo(product: Product) {
        binding.productClickView.setOnClickListener {
            val productStatus = when(orderStatus){
                OrderStatus.RECEIVED_SUCCESSFULLY -> 8
                OrderStatus.DECLINED_BUYER -> 1
                OrderStatus.DECLINED -> 1
                else -> 6
            }
            findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToProductDetailsFragment(
                product = product.apply { status = productStatus }, isSeller = false, productId = null))
        }
        Glide.with(this)
            .load(product.photo[0].photo)
            .into(binding.productImageView)

        binding.productNameTextView.text = product.name
        binding.productConditionTextView.text = product.property?.find {
            it.name == "Состояние"
        }?.value

        binding.sizeTextView.text = product.property?.find {
            Range.create(1, 9).contains(it.id.toInt())
        }?.value.orEmpty()

        val crossedStr = "${product.price} ₽".toSpannable().apply {
            setSpan(
                StrikethroughSpan(), 0, length, 0
            )
        }
        binding.oldPriceTextView.text = crossedStr
        val discountedPrice = when{
            product.statusUser?.price?.status == 1 -> product.statusUser.price.value
            product.statusUser?.sale?.status == 1 -> product.statusUser.sale.value
            else -> null
        }
        if(orderItem.deliveryInfo != "СДЕК")
           binding.newPriceTextView.text = discountedPrice ?: product.priceNew.toString()
        if(orderItem.deliveryInfo != "Заберу у продавца"){
            binding.priceTextView.text = "Цена с учётом доставки"
        }
    }



    private fun setStatusInfo(order: OrderItem){
        with(binding) {
            when (order.status) {
                OrderStatus.DELIVERY -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_package)
                    orderItemStatusContent.text =
                        requireContext().getString(R.string.active_order_waiting_courier)

                }
                OrderStatus.RECEIVED -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_checkcircle)
                    orderItemAcceptBlock.visibility = View.VISIBLE
                    orderItemStatusContent.text = requireContext().getString(R.string.active_order_recivied)
                    address.text = order.newAddress?.substringBefore(";")
                    orderItemAcceptButton.setOnClickListener {
                        YesNoDialog(
                            requireContext().getString(R.string.active_order_accept_dialog_title),
                            null,
                            object : YesNoDialog.onClickListener {
                                override fun onYesClick() {
                                    presenter.submitReceiveOrder(order.id)
                                }

                                override fun onNoClick() {

                                }

                            }).show(childFragmentManager, null)

                         }
                    orderItemAcceptProblemsButton.setOnClickListener {
                        findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToOrderProblemsFragment(order.productId))
                    }
                    orderItemAcceptButton.text = "Подтвердить получение товара"
                    orderItemAcceptProblemsButton.text = "Проблема с товаром"

                }
                OrderStatus.AT_COURIER -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_truck)
                    orderItemStatusContent.text =
                        requireContext().getString(R.string.active_order_at_courier)

                }
                OrderStatus.MEETING_NOT_ACCEPTED -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                    orderItemStatusContent.text =
                        requireContext().getString(R.string.active_order_meeting_not_accepted)

                }
                OrderStatus.CHANGE_MEETING -> {
                    orderItemStatusBlock.isVisible = false //= View.GONE
                    orderItemAcceptBlock.visibility = View.VISIBLE
                    orderItemAcceptDescription.isVisible = false
                    orderItemAcceptButton.text = "Подтвердить место встречи"
                    orderItemAcceptProblemsButton.text = "Обсудить в диалоге"
                    orderItemAcceptButton.setOnClickListener { presenter.acceptOrderPlace(order.id, order.newAddressId!!,
                    order.newAddress.orEmpty(), true) }
                    orderItemAcceptProblemsButton.setOnClickListener {
                        findNavController().navigate(
                            R.id.chatFragment,
                            bundleOf(
                                ChatFragment.USER_ID_KEY to order.user?.id,
                                ChatFragment.PRODUCT_ID_KEY to order.productId,
                                ChatFragment.FROM_ORDERS to true,
                                Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                            )
                        )
                    }

                    address.text =
                        "${order.newAddress?.substringBefore(";")} ${order.newTime}"

                }
                OrderStatus.CHANGE_MEETING_FROM_ME -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                    orderItemStatusContent.text =
                        requireContext().getString(R.string.active_order_meeting_not_accepted)

                    address.text =
                        order.newAddress?.substringBefore(";")

                }
                OrderStatus.NOT_SUBMITTED -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusContent.text = "Ожидание подтверждения от продавца"
                }
                OrderStatus.DECLINED -> {
                    orderItemDeliveryChangeBlock.isVisible = true
                    orderItemChangeDeliveryAcceptButton.isVisible = false
                    orderItemChangeDeliveryDeclineButton.isVisible = false
                    orderItemDeliveryChangeTitle.text = "К сожалению продавец отклонил сделку"
                    cancelTextView.isVisible = false
                    orderItemAcceptBlock.isVisible = false
                    orderItemStatusBlock.isVisible = false

                }
                OrderStatus.DECLINED_BUYER -> {
                    orderItemDeliveryChangeBlock.isVisible = true
                    orderItemChangeDeliveryAcceptButton.isVisible = false
                    orderItemChangeDeliveryDeclineButton.isVisible = false
                    orderItemDeliveryChangeTitle.text = "К сожалению покупатель отклонил сделку"
                    cancelTextView.isVisible = false
                    orderItemStatusBlock.isVisible = false
                    orderItemAcceptBlock.isVisible = false
                }

                OrderStatus.RECEIVED_SUCCESSFULLY -> {
                    address.text = order.newAddress
                    orderItemStatusBlock.isVisible = false
                    orderItemHistoryStatusBlock.isVisible = true
                    cancelTextView.isVisible = false
                    orderItemAcceptBlock.isVisible = false
                }
                OrderStatus.NOT_PAYED -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusContent.text = "Не оплачено"
                    binding.orderItemAcceptBlock.isVisible = true
                    binding.orderItemAcceptDescription.isVisible = false
                    binding.orderItemAcceptButton.text = "Оплатить"
                    binding.orderItemAcceptButton.isEnabled = false
                    binding.orderItemAcceptProblemsButton.isVisible = false

                }
                OrderStatus.DELIVERY_STATUS_NOT_VALID -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusContent.text = "Данные доставки некорректны"
                    binding.orderItemAcceptBlock.isVisible = false


                }
                OrderStatus.DELIVERY_STATUS_ACCEPTED -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusContent.text = order.deliveryStatusInfo
                    binding.orderItemAcceptBlock.isVisible = false


                }
                OrderStatus.DELIVERY_STATUS_NOT_DEFINED -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusContent.text = "Неизвестный статус доставки"
                    binding.orderItemAcceptBlock.isVisible = false

                }
                else -> {}
            }
        }

    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading(loading: Boolean) {
        binding.loader.isVisible = loading
        binding.orderItemAcceptButton.isEnabled = !loading

    }

    override fun orderStatusChanged(status: OrderStatus) {
        args.orderItem?.status = status
        if(args.orderItem != null)
        bind(args.orderItem!!)
        else presenter.getOrderItem(args.orderId)


    }

    override fun productLoaded(product: Product) {
        setProductInfo(product)
    }

    override fun orderItemLoaded(orderItem: OrderItem) {
        bind(orderItem)
        presenter.getProductById(orderItem.productId)
    }

    override fun setProfileInfo(avatar: String, name: String) {
        Glide.with(this)
            .load(avatar)
            .circleCrop()
            .into(binding.sellerAvatarImageView)
        binding.sellerNameTextView.text = name

    }

    override fun supportDialogLoaded(chat: Chat) {
        binding.toolbar.actionIcon.setOnClickListener{
            findNavController().navigate(
                R.id.chatFragment,
                bundleOf(Chat::class.java.simpleName to Chat.Support::class.java.simpleName,
                    ChatFragment.DIALOG_ID_KEY to chat.id)
            )
        }
    }

    override fun payment(paymentUrl: PaymentUrl, orderId: Long) {
        binding.loader.isVisible = false
        binding.webView.isVisible = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return if (request?.url.toString().contains("success")) {
                    view?.isVisible = false
                    findNavController().navigate(
                        OrderDetailsFragmentDirections.actionOrderDetailsFragmentToSuccessOrderPaymentFragment(orderId = orderId))
                    true
                }else if(request?.url.toString().contains("failed")) {
                    showMessage("Ошибка оплаты")
                    true
                }else {
                    false
                }
            }

        }
        binding.webView.settings.userAgentString = "Chrome/56.0.0.0 Mobile"
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(paymentUrl.formUrl)
    }

    override fun setFinalPrice(price: Float) {

        //currentDeliveryPrice = price
        //val sumWithDelivery = orderItem.price + price
        //val finalSum = sumWithDelivery + (sumWithDelivery * 0.05)
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        binding.newPriceTextView.text = "${df.format(price).replace(',', '.')} ₽"
        if(orderItem.status == OrderStatus.NOT_PAYED){
            binding.orderItemAcceptButton.isEnabled = true
            binding.orderItemAcceptButton.setOnClickListener {
                try {
                    val formattedSum = df.format(price).replace(',', '.').toFloat()
                    presenter.getPaymentUrl(
                        formattedSum,
                        orderItem.id
                    )
                }catch (e: Throwable){
                    showMessage(e.message.toString())
                }
            }
        }

    }

    override fun navigateToCreateDelivery(order: Order) {
        findNavController().navigate(OrderDetailsFragmentDirections
            .actionOrderDetailsFragmentToCreateDeliveryFragment(order))
    }


    private fun setAddress(order: OrderItem){
        Log.d("Mylog", order.newAddress.orEmpty())
        val address  = if(order.newAddress != null) order.newAddress?.substringBefore(";")
                               else order.cdekYandexAddress
        binding.address.text = address
        if(order.newAddress == null && order.cdekYandexAddress == null) {
            binding.myAddressBlock.isVisible = false
            binding.dealPlaceTextView.isVisible = false
        }else{
            binding.myAddressBlock.isVisible = true
            binding.dealPlaceTextView.isVisible = true

        }
        /*if(order.deliveryInfo == "yandex"){
            binding.icon.setImageResource(R.drawable.ic_package)
            binding.icon.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white, null))
            binding.icon.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue4, null))
            binding.title.setText(R.string.myAddress_pickup)
        }*/
        //if(order.deliveryInfo == "СДЕК"){
            if(address?.contains("cdek code:") == true) {
                binding.icon.setImageResource(R.drawable.ic_envelope)
                binding.icon.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.green, null))
                binding.title.setText(R.string.myAddress_delivery)
            }else {
                binding.icon.setImageResource(R.drawable.ic_package)
                binding.icon.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white, null))
                binding.icon.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.blue4, null))
                binding.title.setText(R.string.myAddress_pickup)
            }
        //}

    }

    private fun setOrderStatus(order: OrderItem){
        when(order.status){
          OrderStatus.NOT_SUBMITTED_SELLER -> setNotSubmittedStatus(order)
          OrderStatus.MEETING_NOT_ACCEPTED_SELLER -> setMeetingNotAcceptedSellerStatus()
          OrderStatus.CHANGE_MEETING_SELLER -> setChangeMeetingSellerStatus(order)
          OrderStatus.RECEIVED_SELLER -> setReceivedSellerStatus()
          OrderStatus.ADD_MEETING ->  setAddMeetingStatus(order)
            OrderStatus.NOT_PAYED_SELLER -> setNotPayedSellerStatus()
            OrderStatus.CREATE_DELIVERY -> setCreateDeliverySellerStatus()
            else -> {}
        }
    }

    private fun setNotSubmittedStatus(order: OrderItem){
        binding.orderItemStatusBlock.isVisible = true
        binding.orderItemStatusContent.text = "Необходимо подтвердить наличие товара"
        binding.orderItemStatusContent.setTextColor(resources.getColor(R.color.black))
        binding.orderItemStatusImage.setImageResource(R.drawable.ic_warning)
        binding.orderItemStatusImage.drawable.setTint(resources.getColor(R.color.orange))
        binding.orderItemAcceptBlock.isVisible = true
        binding.orderItemAcceptDescription.isVisible = false
        binding.orderItemAcceptButton.text = "Подтвердить"
        binding.orderItemAcceptProblemsButton.text = "Отменить"
        binding.orderItemAcceptButton.setOnClickListener{
            if(order.chatFunctionInfo != null)
            presenter.submitBuy(order)
        }
        binding.orderItemAcceptProblemsButton.setOnClickListener{
            if(order.chatFunctionInfo != null)
            presenter.cancelBuyRequest(order)
        }
    }

    private fun setMeetingNotAcceptedSellerStatus(){
        binding.orderItemAcceptBlock.isVisible = false
        binding.orderItemStatusContent.text = "Покупатель ещё не подтвердил место встречи"
    }

    private fun setChangeMeetingSellerStatus(order: OrderItem){
        binding.orderItemStatusBlock.isVisible = false
        binding.orderItemAcceptDescription.isVisible = false
        binding.orderItemAcceptButton.text = "Принять место встречи"
        binding.orderItemAcceptProblemsButton.text = "Обсудить в диалоге"
        binding.orderItemAcceptButton.setOnClickListener{
            presenter.acceptOrderPlace(
                order.id,
                order.newAddressId!!,
                order.newAddress!!,
                false
            )
        }
    }

    private fun setReceivedSellerStatus(){
        binding.orderItemStatusBlock.isVisible = true
        binding.orderItemStatusContent.text =
            "Ожидание встречи с покупателем"
    }

    private fun setAddMeetingStatus(order: OrderItem){
        binding.orderItemStatusBlock.isVisible = true
        binding.orderItemStatusContent.text = "Необходимо добавить место встречи"
        binding.orderItemAcceptBlock.isVisible = true
        binding.orderItemAcceptDescription.isVisible = false
        binding.orderItemAcceptButton.text = "Добавить место встречи"
        binding.orderItemAcceptProblemsButton.isVisible = false
        setFragmentResultListener(DealPlaceFragment.ADDRESS_REQUEST){_, bundle ->
            binding.myAddressBlock.isVisible = true
            order.status =  OrderStatus.MEETING_NOT_ACCEPTED_SELLER
            order.newAddress = bundle.getString(DealPlaceFragment.ADDRESS)
            setAddress(order)
            setOrderStatus(order)
        }
        binding.orderItemAcceptButton.setOnClickListener{
            findNavController().navigate(OrderDetailsFragmentDirections.actionOrderDetailsFragmentToDealPlaceFragment(order.id))
        }
    }

    private fun setNotPayedSellerStatus(){
        binding.orderItemStatusBlock.isVisible = true
        binding.orderItemStatusContent.text = "Не оплачено"

    }

    private fun setCreateDeliverySellerStatus(){
        binding.orderItemStatusBlock.isVisible = true
        binding.orderItemStatusContent.text = "Оплачено"
        binding.orderItemAcceptBlock.isVisible = true
        binding.orderItemAcceptDescription.isVisible = false
        binding.orderItemAcceptButton.text = "Подтвердить доставку"
        binding.orderItemAcceptProblemsButton.isVisible = false
        binding.orderItemAcceptButton.setOnClickListener {
           presenter.getOrderForDelivery(orderItem.id)
        }
    }

    private fun getDeliveryPrice(order: OrderItem){
        if(order.deliveryInfo == "СДЕК")
            presenter.getFinalCdekPrice(toAddress = order.cdekYandexAddress ?: "", product = order.product, promo = order.promo)
        if(order.deliveryInfo == "yandex")
            presenter.getFinalYandexGoPrice(toAddress = order.cdekYandexAddress ?: "", product = order.product, promo = order.promo)
    }
}