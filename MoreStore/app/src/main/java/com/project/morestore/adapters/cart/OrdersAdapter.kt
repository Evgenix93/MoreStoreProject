package com.project.morestore.adapters.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                        orderItemStatusImage.setImageResource(R.drawable.ic_package)
                        orderItemStatusContent.text = context.getString(R.string.active_order_waiting_courier)
                    }
                    OrderStatus.RECEIVED -> {
                        orderItemStatusImage.setImageResource(R.drawable.ic_checkcircle)
                        orderItemAcceptBlock.visibility = View.VISIBLE
                        orderItemStatusContent.text = context.getString(R.string.active_order_recivied)
                    }
                    OrderStatus.AT_COURIER -> {
                        orderItemStatusImage.setImageResource(R.drawable.ic_truck)
                        orderItemStatusContent.text = context.getString(R.string.active_order_at_courier)
                    }
                    OrderStatus.MEETING_NOT_ACCEPTED -> {
                        orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                        orderItemStatusContent.text = context.getString(R.string.active_order_meeting_not_accepted)
                    }
                    OrderStatus.CHANGE_MEETING -> {
                        orderItemStatusBlock.visibility = View.GONE
                        orderItemDeliveryChangeBlock.visibility = View.VISIBLE
                        orderItemDeliveryChangeContent.text =
                            "${orderItem.newAddress} ${orderItem.newTime}"
                    }
                    OrderStatus.CHANGE_MEETING_FROM_ME -> {
                        orderItemStatusImage.setImageResource(R.drawable.ic_clock)
                        orderItemStatusContent.text = context.getString(R.string.active_order_meeting_not_accepted)
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