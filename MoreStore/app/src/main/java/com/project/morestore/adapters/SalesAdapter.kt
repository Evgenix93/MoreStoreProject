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
import com.project.morestore.models.*
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import java.sql.Time
import java.util.*

class SalesAdapter(private val isHistory: Boolean, private val addDealPlace:(orderId: Long) -> Unit, private val acceptDealPlace:(OfferedOrderPlaceChange) -> Unit, private val declineDealPlace:(Long, Long) -> Unit): RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {
    private var sales = listOf<Order>()
    private var addresses = listOf<OfferedOrderPlace>()
    private var users = listOf<User?>()

    inner class SaleViewHolder(view: View): RecyclerView.ViewHolder(view){
        val binding: ItemOrderBinding by viewBinding()

        init {
          binding.orderItemAcceptBlock.isVisible = true
          binding.orderItemAcceptDescription.isVisible = false
            binding.orderItemAcceptButton.isVisible = isHistory.not()
          binding.orderItemAcceptProblemsButton.isVisible = false
          binding.orderItemAcceptButton.text = "Добавить место встречи"
          binding.orderItemDeliveryDateText.text = "Самовывоз"
          binding.orderItemDeliveryDateText.setTextColor(ResourcesCompat.getColor(itemView.resources, R.color.black, null))
          binding.orderItemDeliveryDate.text = "Доставка"
          binding.orderItemDeliveryContent.text = "по желанию продавца"
          binding.orderItemStatusContent.text = "Необходимо добавить место встречи"
          binding.orderItemDeliveryTitle.text = "Адрес:"


            if(isHistory){
                binding.orderItemStatusImage.setImageResource(R.drawable.ic_fill_checkcircle)
                binding.orderItemStatusImage.imageTintList = null
                binding.orderItemStatusContent.setTextColor(ResourcesCompat.getColor(itemView.resources, R.color.black, null))
                binding.orderItemStatusContent.text = "Сделка завершена"

            }
        }

        fun bind(order: Order, address: OfferedOrderPlace?, user: User?){
           binding.orderItemName.text = order.cart?.get(0)?.name
           binding.orderItemPriceText.text = "${order.cart?.get(0)?.priceNew?.toInt()}Р"
            binding.orderItemAcceptButton.setOnClickListener{
                addDealPlace(order.id)
            }
            address?.let{offeredOrderPlace ->
                val timeStamp = offeredOrderPlace.address.substringAfter(';').toLongOrNull()
                if(timeStamp != null) {
                    val calendar = Calendar.getInstance()
                        .apply { timeInMillis = timeStamp }
                    val hours = calendar.get(Calendar.HOUR_OF_DAY)
                    val minutes = calendar.get(Calendar.MINUTE)
                    binding.orderItemDeliveryContent.text =
                        "${offeredOrderPlace.address.substringBefore(';')}, $hours:$minutes"
                }else
                    binding.orderItemDeliveryContent.text = offeredOrderPlace.address
                binding.orderItemAcceptButton.isVisible = false
                when(address.type){
                    OfferedPlaceType.PROPOSED.value -> {
                        binding.orderItemDeliveryChangeBlock.isVisible = false
                        binding.orderItemStatusBlock.isVisible = true
                        if(address.status == 0)
                            binding.orderItemStatusContent.text = "Покупатель ещё не подтвердил место встречи"
                        else
                            binding.orderItemStatusContent.text = "Ожидание встречи с покупателем"
                    }
                    OfferedPlaceType.APPLICATION.value -> {
                       if(address.status == 0) {
                           binding.orderItemDeliveryChangeBlock.isVisible = true
                           binding.orderItemDeliveryChangeTitle.text =
                               "Покупатель предложил место встречи"
                           binding.orderItemDeliveryChangeContent.isVisible = false
                           binding.orderItemStatusBlock.isVisible = false
                           binding.orderItemChangeDeliveryAcceptButton.setOnClickListener{
                               acceptDealPlace(OfferedOrderPlaceChange(
                                   idOrder = order.id,
                                   idAddress = offeredOrderPlace.id,
                                   address = offeredOrderPlace.address,
                                   status = 1
                               ))
                           }
                           binding.orderItemChangeDeliveryDeclineButton.setOnClickListener{
                               user?.let{
                                declineDealPlace(it.id, order.cart!![0].id)
                               }
                           }
                       }else
                           binding.orderItemStatusContent.text = "Ожидание встречи с покупателем"
                    }
                }
            }

            binding.orderItemUserName.text = "${user?.name} ${user?.surname}"
            Glide.with(itemView)
                .load(user?.avatar?.photo)
                .into(binding.orderItemUserIcon)

           Glide.with(itemView)
               .load(order.cart?.get(0)?.photo?.get(0)?.photo)
               .into(binding.orderItemPreview)
        }
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
         return SaleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))
     }

     override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val address = addresses.find { sales[position].id == it.idOrder  }
        holder.bind(sales[position], address, users[position])
     }

     override fun getItemCount(): Int {
         return sales.size
     }

     fun updateList(newList: List<Order>, newAddresses: List<OfferedOrderPlace>, newAvatars: List<User?>){
        sales = newList
        addresses = newAddresses
        users = newAvatars
        notifyDataSetChanged()
    }
 }