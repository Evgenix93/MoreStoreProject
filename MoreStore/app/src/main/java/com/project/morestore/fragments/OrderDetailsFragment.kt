package com.project.morestore.fragments

import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.util.Range
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOrderDetailsBinding
import com.project.morestore.dialogs.DeleteDialog
import com.project.morestore.fragments.orders.active.OrdersActiveFragmentDirections
import com.project.morestore.models.Chat
import com.project.morestore.models.Order
import com.project.morestore.models.Product
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import com.project.morestore.mvpviews.OrderDetailsView
import com.project.morestore.presenters.OrderDetailsPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class OrderDetailsFragment: MvpAppCompatFragment(R.layout.fragment_order_details), OrderDetailsView {
    private val binding: FragmentOrderDetailsBinding by viewBinding()
    private val args: OrderDetailsFragmentArgs by navArgs()
    private val presenter by moxyPresenter { OrderDetailsPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind(args.orderItem)
    }


    private fun bind(order: OrderItem) {
        setProductInfo(order.product)
        binding.chosenDeliveryTypeTextView.text = order.deliveryInfo
        setStatusInfo(order)
        binding.sellerAvatarImageView.setOnClickListener {
            if (order.user != null)
                findNavController().navigate(
                    OrderDetailsFragmentDirections.actionOrderDetailsFragmentToSellerProfileFragment(
                        order.user,
                        false
                    )
                )

        }

        binding.cancelTextView.setOnClickListener {
            DeleteDialog(context = requireContext(),
                title = "Отменить сделку?",
                message = "Товар будет снова доступен для покупки другими покупателями.",
                confirmCallback = {
                    if (order.chatFunctionInfo != null) presenter.cancelBuyRequest(order.chatFunctionInfo,
                    order.product.idUser!!)
                }
            )
        }
    }

    private fun setProductInfo(product: Product) {
        Glide.with(this)
            .load(product.user?.avatar?.photo.toString())
            .into(binding.sellerAvatarImageView)
        binding.sellerNameTextView.text = product.user?.name


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
        binding.newPriceTextView.text = product.priceNew.toString()
    }

    /*private fun setDeliveryTypeInfo(delivery: Int){
        binding.chosenDeliveryTypeTextView.text = when(delivery){
            1 -> "заберу у продавца"
            2 -> "по городу"
            3 -> {
                binding.deliveryIcon.isVisible = true
                "в другой город"
            }

        }

    }*/

    private fun setStatusInfo(order: OrderItem){
        with(binding) {
            when (order.status) {
                OrderStatus.DELIVERY -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_package)
                    orderItemStatusContent.text =
                        requireContext().getString(R.string.active_order_waiting_courier)
                    //orderItemDeliveryAddress.isVisible = false
                    //orderItemDeliveryAddressContent.isVisible = false
                    //orderItemAcceptBlock.isVisible = false
                    //orderItemDeliveryChangeBlock.isVisible = false
                }
                OrderStatus.RECEIVED -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_checkcircle)
                    orderItemAcceptBlock.visibility = View.VISIBLE
                    orderItemStatusContent.text = requireContext().getString(R.string.active_order_recivied)
                    address.text = order.newAddress?.substringBefore(";")
                    orderItemAcceptButton.setOnClickListener { presenter.submitReceiveOrder(order.id) }
                //orderItemDeliveryAddress.isVisible = false
                    //orderItemDeliveryAddressContent.isVisible = false
                    //orderItemDeliveryChangeBlock.isVisible = false
                }
                OrderStatus.AT_COURIER -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_truck)
                    orderItemStatusContent.text =
                        requireContext().getString(R.string.active_order_at_courier)
                    //orderItemDeliveryAddress.isVisible = false
                    //orderItemDeliveryAddressContent.isVisible = false
                    //orderItemDeliveryChangeBlock.isVisible = false
                    //orderItemAcceptBlock.isVisible = false
                }
                OrderStatus.MEETING_NOT_ACCEPTED -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                    orderItemStatusContent.text =
                        requireContext().getString(R.string.active_order_meeting_not_accepted)
                    icon.isVisible = false
                    title.isVisible = false
                    name.isVisible = false
                    address.isVisible = false
                    //orderItemDeliveryAddress.isVisible = false
                    //orderItemDeliveryAddressContent.isVisible = false
                    //orderItemAcceptBlock.isVisible = false
                    //orderItemDeliveryChangeBlock.isVisible = false
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
                    //orderItemChangeDeliveryAcceptButton.isVisible = true
                    //orderItemChangeDeliveryDeclineButton.isVisible = true
                    address.text =
                        "${order.newAddress?.substringBefore(";")} ${order.newTime}"
                    //orderItemDeliveryAddress.isVisible = false
                    //orderItemDeliveryAddressContent.isVisible = false
                    //orderItemAcceptBlock.isVisible = false
                }
                OrderStatus.CHANGE_MEETING_FROM_ME -> {
                    orderItemStatusBlock.isVisible = true
                    orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                    orderItemStatusContent.text =
                        requireContext().getString(R.string.active_order_meeting_not_accepted)
                            //orderItemDeliveryAddress.isVisible = true
                    //orderItemDeliveryAddressContent.isVisible = true
                    address.text =
                        order.newAddress?.substringBefore(";")
                    //orderItemAcceptBlock.isVisible = false
                    //orderItemDeliveryChangeBlock.isVisible = false
                }
                OrderStatus.NOT_SUBMITTED -> {
                    orderItemStatusBlock.isVisible = true
                    icon.isVisible = false
                    title.isVisible = false
                    name.isVisible = false
                    address.isVisible = false
                   // orderItemAcceptBlock.isVisible = false
                    //orderItemDeliveryChangeBlock.isVisible = false
                    orderItemStatusContent.text = "Ожидание подтверждения от продавца"
                }
                OrderStatus.DECLINED -> {
                    //orderItemStatusBlock.isVisible = false
                    //orderItemAcceptBlock.isVisible = false

                    orderItemDeliveryChangeBlock.isVisible = true
                    orderItemChangeDeliveryAcceptButton.isVisible = false
                    orderItemChangeDeliveryDeclineButton.isVisible = false
                    orderItemDeliveryChangeTitle.text = "Сделка отменена"
                    icon.isVisible = false
                    title.isVisible = false
                    name.isVisible = false
                    address.isVisible = false
                }
                OrderStatus.RECEIVED_SUCCESSFULLY -> {
                    address.text = order.newAddress
                    orderItemStatusBlock.isVisible = false
                    orderItemHistoryStatusBlock.isVisible = true


                }
            }
        }

    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun loading(loading: Boolean) {

    }

    override fun orderStatusChanged(status: OrderStatus) {
        args.orderItem.status = status
        bind(args.orderItem)
    }
}