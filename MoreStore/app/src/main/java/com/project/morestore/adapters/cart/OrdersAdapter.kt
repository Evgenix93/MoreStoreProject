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
import com.project.morestore.models.User
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus

class OrdersAdapter(
    private val items: List<OrderItem>,
    private val onClickListener: OrderClickListener,
    private val onProfileClick: (User) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderItemHolder>() {

    inner class OrderItemHolder(menuItem: View) : RecyclerView.ViewHolder(menuItem) {
        val binding: ItemOrderBinding by viewBinding()

        fun bind(orderItem: OrderItem) {
            with(binding) {
                orderItemId.text = "№ ${orderItem.id}"

                //orderItemUserIcon.setImageBitmap(orderItem.userIcon)
                Glide.with(itemView).load(orderItem.user?.avatar?.photo).circleCrop().into(orderItemUserIcon)
                orderItemUserName.text = orderItem.user?.name
                //orderItemPreview.setImageBitmap(orderItem.photo)
                Glide.with(itemView).load(orderItem.photo).into(orderItemPreview)
                orderItemName.text = orderItem.name
                orderItemDeliveryContent.text = orderItem.deliveryInfo

                orderItemPriceText.text = "${orderItem.price} ₽"
                orderItemDeliveryDateText.text = orderItem.deliveryDate

                orderItemUserIcon.setOnClickListener { if(orderItem.user != null) onProfileClick(orderItem.user) }
                orderItemUserName.setOnClickListener { if(orderItem.user != null) onProfileClick(orderItem.user) }

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
                        orderItemStatusContent.text = "Ожидание подтверждения от продавца"
                    }
                    OrderStatus.DECLINED -> {
                        orderItemStatusBlock.isVisible = false
                        orderItemAcceptBlock.isVisible = false
                        orderItemDeliveryChangeBlock.isVisible = true
                        orderItemChangeDeliveryAcceptButton.isVisible = false
                        orderItemChangeDeliveryDeclineButton.isVisible = false
                        orderItemDeliveryChangeTitle.text = "Сделка отменена"
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