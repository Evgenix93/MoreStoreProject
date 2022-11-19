package com.project.morestore.adapters.cart

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.models.User
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus

class OrdersAdapter(
    private val items: List<OrderItem>,
    private val onClickListener: OrderClickListener,
    private val onProfileClick: (User) -> Unit,
    private val onClick: (OrderItem) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderItemHolder>() {

    inner class OrderItemHolder(menuItem: View) : RecyclerView.ViewHolder(menuItem) {
        val binding: com.project.morestore.databinding.ItemOrderBinding by viewBinding()
        init {
            itemView.setOnClickListener { onClick(items[adapterPosition]) }
        }

        fun bind(orderItem: OrderItem) {
            with(binding) {
                orderItemId.text = "№ ${orderItem.id}"
                Glide.with(itemView).load(orderItem.user?.avatar?.photo).circleCrop().into(orderItemUserIcon)
                orderItemUserName.text = orderItem.user?.name
                Glide.with(itemView).load(orderItem.photo).into(orderItemPreview)
                orderItemName.text = orderItem.name
                orderItemDeliveryContent.text = orderItem.deliveryInfo

                orderItemPriceText.text = "${orderItem.price} ₽"
                orderItemDeliveryDateText.text = orderItem.deliveryDate

                orderItemUserIcon.setOnClickListener { if(orderItem.user != null) onProfileClick(orderItem.user) }
                orderItemUserName.setOnClickListener { if(orderItem.user != null) onProfileClick(orderItem.user) }

                val context = itemView.context;
                Log.d("orderStatus", orderItem.status.name)
                when (orderItem.status) {
                    OrderStatus.DELIVERY -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemStatusImage.setImageResource(R.drawable.ic_package)
                        orderItemStatusContent.text = context.getString(R.string.active_order_waiting_courier)
                        orderItemDeliveryAddress.isVisible = false
                        orderItemDeliveryAddressContent.isVisible = false
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                    }
                    OrderStatus.RECEIVED -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemStatusImage.setImageResource(R.drawable.ic_checkcircle)
                        orderItemAcceptBlock.visibility = View.VISIBLE
                        orderItemPayButton.isVisible = false
                        orderItemAcceptButton.isVisible = true
                        orderItemAcceptProblemsButton.isVisible = true
                        orderItemAcceptDescription.isVisible = true
                        orderItemStatusContent.text = context.getString(R.string.active_order_recivied)
                        orderItemDeliveryAddress.isVisible = false
                        orderItemDeliveryAddressContent.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                    }
                    OrderStatus.AT_COURIER -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemStatusImage.setImageResource(R.drawable.ic_truck)
                        orderItemStatusContent.text = context.getString(R.string.active_order_at_courier)
                        orderItemDeliveryAddress.isVisible = false
                        orderItemDeliveryAddressContent.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                        orderItemAcceptBlock.isVisible = false
                    }
                    OrderStatus.MEETING_NOT_ACCEPTED -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                        orderItemStatusContent.text = context.getString(R.string.active_order_meeting_not_accepted)
                        orderItemDeliveryAddress.isVisible = false
                        orderItemDeliveryAddressContent.isVisible = false
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                    }
                    OrderStatus.CHANGE_MEETING -> {
                        orderItemStatusBlock.isVisible = false //= View.GONE
                        orderItemDeliveryChangeBlock.visibility = View.VISIBLE
                        orderItemChangeDeliveryAcceptButton.isVisible = true
                        orderItemChangeDeliveryDeclineButton.isVisible = true
                        orderItemDeliveryChangeContent.text =
                            "${orderItem.newAddress?.substringBefore(";")} ${orderItem.newTime}"
                        orderItemDeliveryAddress.isVisible = false
                        orderItemDeliveryAddressContent.isVisible = false
                        orderItemAcceptBlock.isVisible = false
                    }
                    OrderStatus.CHANGE_MEETING_FROM_ME -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                        orderItemStatusContent.text = context.getString(R.string.active_order_meeting_not_accepted)
                        orderItemDeliveryAddress.isVisible = true
                        orderItemDeliveryAddressContent.isVisible = true
                        orderItemDeliveryAddressContent.text = orderItem.newAddress?.substringBefore(";")
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                    }
                    OrderStatus.NOT_SUBMITTED -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                        orderItemStatusContent.text = itemView.context.getString(R.string.awaiting_seller_submition)
                    }
                    OrderStatus.DECLINED -> {
                        orderItemStatusBlock.isVisible = false
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = true
                        orderItemChangeDeliveryAcceptButton.isVisible = false
                        orderItemChangeDeliveryDeclineButton.isVisible = false
                        orderItemDeliveryChangeTitle.text = itemView.context.getString(R.string.seller_decline)
                    }
                    OrderStatus.DECLINED_BUYER -> {
                        orderItemStatusBlock.isVisible = false
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = true
                        orderItemChangeDeliveryAcceptButton.isVisible = false
                        orderItemChangeDeliveryDeclineButton.isVisible = false
                        orderItemDeliveryChangeTitle.text = itemView.context.getString(R.string.buyer_cancel_deal)
                    }
                    OrderStatus.NOT_PAYED -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemDeliveryChangeBlock.isVisible = false
                        orderItemAcceptBlock.isVisible = true
                        orderItemPayButton.isVisible = true
                        orderItemAcceptButton.isVisible = false
                        orderItemAcceptProblemsButton.isVisible = false
                        orderItemAcceptDescription.isVisible = false
                        orderItemStatusContent.text = itemView.context.getString(R.string.not_paid)
                        orderItemStatusImage.setImageResource(R.drawable.ic_credit_card)
                        orderItemStatusImage.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.blue4, null))

                    }
                    OrderStatus.DELIVERY_STATUS_NOT_VALID -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                        orderItemStatusContent.text = itemView.context.getString(R.string.delivery_data_not_correct)
                        orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                        Log.d("deliveryLog", "not valid")

                    }
                    OrderStatus.DELIVERY_STATUS_ACCEPTED -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                        orderItemStatusContent.text = orderItem.deliveryStatusInfo
                        orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                        Log.d("deliveryLog", orderItem.deliveryStatusInfo.toString() )

                    }
                    OrderStatus.DELIVERY_STATUS_NOT_DEFINED -> {
                        orderItemStatusBlock.isVisible = true
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = false
                        orderItemStatusContent.text = itemView.context.getString(R.string.uknown_delivery_status)
                        orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                        Log.d("deliveryLog", "not defined")

                    }
                    else -> {}
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Recycler impl
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
        return OrderItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderItemHolder, position: Int) {
        holder.bind(items[position])
        holder.binding.orderItemChangeDeliveryAcceptButton.setOnClickListener() {
            onClickListener.acceptMeeting(items[position])
        }
        holder.binding.orderItemChangeDeliveryDeclineButton.setOnClickListener() {
            onClickListener.declineMeeting(items[position])
        }
        holder.binding.orderItemAcceptButton.setOnClickListener() {
            onClickListener.acceptOrder(items[position])
        }
        holder.binding.orderItemAcceptProblemsButton.setOnClickListener() {
            onClickListener.reportProblem(items[position])
        }
        holder.binding.orderItemPayButton.setOnClickListener {
            onClickListener.payForOrder(items[position])
        }
    }

    override fun getItemCount() = items.size
}