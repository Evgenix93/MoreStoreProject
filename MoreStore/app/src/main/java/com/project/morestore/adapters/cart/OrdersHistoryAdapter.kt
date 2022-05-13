package com.project.morestore.adapters.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemOrderHistoryBinding
import com.project.morestore.models.cart.OrderHistoryItem

class OrdersHistoryAdapter(
    private val items: List<OrderHistoryItem>,
    private val onDeliveryClickListener: (OrderHistoryItem) -> Unit
) : RecyclerView.Adapter<OrdersHistoryAdapter.OrderHistoryItemHolder>() {

    class OrderHistoryItemHolder(menuItem: View) : RecyclerView.ViewHolder(menuItem) {
        val binding: ItemOrderHistoryBinding by viewBinding()

        fun bind(orderHistoryItem: OrderHistoryItem) {
            with(binding) {
                orderItemHistoryId.text = orderHistoryItem.id
                //orderItemHistoryUserIcon.setImageBitmap(orderHistoryItem.userIcon)
                Glide.with(itemView).load(orderHistoryItem.userIcon).into(orderItemHistoryUserIcon)
                orderItemHistoryUserName.text = orderHistoryItem.userName
                //orderItemHistoryPreview.setImageBitmap(orderHistoryItem.photo)
                Glide.with(itemView).load(orderHistoryItem.photo).into(orderItemHistoryPreview)
                orderItemHistoryName.text = orderHistoryItem.name
                orderItemHistoryPriceFinal.text = "${orderHistoryItem.price} ₽"
                orderItemHistoryDeliveryDate.text = orderHistoryItem.deliveryDate
                orderItemHistoryDeliveryContent.text = orderHistoryItem.deliveryInfo
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Recycler impl
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryItemHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_history, parent, false)
        return OrderHistoryItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderHistoryItemHolder, position: Int) {
        holder.bind(items[position])
        holder.binding.orderItemHistoryDeliveryBlock.setOnClickListener() {
            onDeliveryClickListener(items[position])
        }
    }

    override fun getItemCount() = items.size
}