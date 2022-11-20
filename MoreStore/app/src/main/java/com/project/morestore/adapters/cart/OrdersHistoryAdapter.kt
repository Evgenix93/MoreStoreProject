package com.project.morestore.adapters.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemOrderHistoryBinding
import com.project.morestore.data.models.User
import com.project.morestore.data.models.cart.OrderHistoryItem
import com.project.morestore.data.models.cart.OrderItem
import com.project.morestore.data.models.cart.OrderStatus

class OrdersHistoryAdapter(
    private val items: List<OrderHistoryItem>,
    private val onDeliveryClickListener: (OrderHistoryItem) -> Unit,
    private val onProfileClick: (User) -> Unit,
    private val onClick: (OrderItem) -> Unit
) : RecyclerView.Adapter<OrdersHistoryAdapter.OrderHistoryItemHolder>() {

    inner class OrderHistoryItemHolder(menuItem: View) : RecyclerView.ViewHolder(menuItem) {
        val binding: ItemOrderHistoryBinding by viewBinding()

        init {
            itemView.setOnClickListener {
                onClick(
                    OrderItem(
                    id = items[adapterPosition].id.toLong(),
                    user = items[adapterPosition].user,
                    product = items[adapterPosition].product,
                    deliveryInfo = items[adapterPosition].deliveryInfo,
                    deliveryDate = items[adapterPosition].deliveryDate,
                    name = items[adapterPosition].name,
                    newAddressId = items[adapterPosition].newAddressId,
                    newAddress = items[adapterPosition].newAddress,
                    photo = items[adapterPosition].photo,
                    productId = items[adapterPosition].productId,
                    newTime = items[adapterPosition].newTime,
                    sellerId = items[adapterPosition].sellerId,
                    price = items[adapterPosition].price,
                    status = OrderStatus.RECEIVED_SUCCESSFULLY
                )
                )
            }
        }

        fun bind(orderHistoryItem: OrderHistoryItem) {
            with(binding) {
                orderItemHistoryId.text = orderHistoryItem.id
                //orderItemHistoryUserIcon.setImageBitmap(orderHistoryItem.userIcon)
                Glide.with(itemView).load(orderHistoryItem.user?.avatar?.photo).circleCrop().into(orderItemHistoryUserIcon)
                orderItemHistoryUserName.text = orderHistoryItem.user?.name
                //orderItemHistoryPreview.setImageBitmap(orderHistoryItem.photo)
                Glide.with(itemView).load(orderHistoryItem.photo).into(orderItemHistoryPreview)
                orderItemHistoryName.text = orderHistoryItem.name
                orderItemHistoryPriceFinal.text = "${orderHistoryItem.price} ₽"
                orderItemHistoryDeliveryDate.text = orderHistoryItem.deliveryDate
                orderItemHistoryDeliveryContent.text = orderHistoryItem.deliveryInfo
                orderItemHistoryUserIcon.setOnClickListener {
                    if(orderHistoryItem.user != null) onProfileClick(orderHistoryItem.user) }
                orderItemHistoryUserName.setOnClickListener {
                    if(orderHistoryItem.user != null) onProfileClick(orderHistoryItem.user)
                }
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