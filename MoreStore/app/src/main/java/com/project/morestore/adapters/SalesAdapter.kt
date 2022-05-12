package com.project.morestore.adapters

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemOrderBinding
import com.project.morestore.models.DealPlace
import com.project.morestore.models.Order
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus

class SalesAdapter(private val isHistory: Boolean, private val addDealPlace:(orderId: Long) -> Unit): RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {
    private var sales = listOf<Order>()
    private var addresses = listOf<DealPlace>()

    inner class SaleViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding: ItemOrderBinding by viewBinding()

        init {
          binding.orderItemAcceptBlock.isVisible = true
          binding.orderItemAcceptDescription.isVisible = false
          binding.orderItemAcceptProblemsButton.isVisible = false
          binding.orderItemAcceptButton.isVisible = isHistory.not()
          binding.orderItemAcceptButton.text = "Добавить место встречи"
          binding.orderItemDeliveryDateText.text = "Самовывоз"
          binding.orderItemDeliveryDateText.setTextColor(ResourcesCompat.getColor(itemView.resources, R.color.black, null))
          binding.orderItemDeliveryDate.text = "Доставка"
          binding.orderItemDeliveryContent.text = "по желанию продавца"
          binding.orderItemStatusContent.text = "Необходимо добавить место встречи"

            if(isHistory){
                binding.orderItemStatusImage.setImageResource(R.drawable.ic_fill_checkcircle)
                binding.orderItemStatusImage.imageTintList = null
                binding.orderItemStatusContent.setTextColor(ResourcesCompat.getColor(itemView.resources, R.color.black, null))
                binding.orderItemStatusContent.text = "Сделка завершена"
            }
        }

        fun bind(order: Order, address: DealPlace?){
           binding.orderItemName.text = order.cart[0].name
           binding.orderItemPriceText.text = "${order.cart[0].priceNew?.toInt()}Р"
            binding.orderItemAcceptButton.setOnClickListener{
                addDealPlace(order.id)
            }
            address?.let{
                binding.orderItemDeliveryContent.text = it.address
            }
           Glide.with(itemView)
               .load(order.cart[0].photo[0].photo)
               .into(binding.orderItemPreview)
        }
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
         return SaleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))
     }

     override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
         holder.bind(sales[position], null)
     }

     override fun getItemCount(): Int {
         return sales.size
     }

     fun updateList(newList: List<Order>){
        sales = newList
        notifyDataSetChanged()
    }
 }