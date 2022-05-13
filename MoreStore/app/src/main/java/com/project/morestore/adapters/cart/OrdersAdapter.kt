package com.project.morestore.adapters.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemOrderBinding
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus

class OrdersAdapter(
    private val items: List<OrderItem>,
    private val onClickListener: OrderClickListener
) : RecyclerView.Adapter<OrdersAdapter.OrderItemHolder>() {

    class OrderItemHolder(menuItem: View) : RecyclerView.ViewHolder(menuItem) {
        val binding: ItemOrderBinding by viewBinding()

        fun bind(orderItem: OrderItem) {
            with(binding) {
                orderItemId.text = "№ ${orderItem.id}"

                //orderItemUserIcon.setImageBitmap(orderItem.userIcon)
                Glide.with(itemView).load(orderItem.userIcon).into(orderItemUserIcon)
                orderItemUserName.text = orderItem.userName
                //orderItemPreview.setImageBitmap(orderItem.photo)
                Glide.with(itemView).load(orderItem.photo).into(orderItemPreview)
                orderItemName.text = orderItem.name
                orderItemDeliveryContent.text = orderItem.deliveryInfo

                orderItemPriceText.text = "${orderItem.price} ₽"
                orderItemDeliveryDateText.text = orderItem.deliveryDate

                val context = itemView.context;
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
                        orderItemStatusBlock.visibility = View.GONE
                        orderItemDeliveryChangeBlock.visibility = View.VISIBLE
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
    }

    override fun getItemCount() = items.size
}