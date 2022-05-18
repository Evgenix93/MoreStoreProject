package com.project.morestore.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemOrderBinding
import com.project.morestore.models.*
import java.util.*

class SalesAdapter(
    private val isHistory: Boolean,
    private val addDealPlace: (orderId: Long) -> Unit,
    private val acceptDealPlace: (OfferedOrderPlaceChange) -> Unit,
    private val declineDealPlace: (Long, Long) -> Unit,
    private val acceptDeal:(ChatFunctionInfo) -> Unit,
    private val cancelDeal:(ChatFunctionInfo) -> Unit,
) : RecyclerView.Adapter<SalesAdapter.SaleViewHolder>() {
    private var sales = listOf<Order>()
    private var addresses = listOf<OfferedOrderPlace>()
    private var users = listOf<User?>()
    private var dialogs = listOf<DialogWrapper>()

    inner class SaleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ItemOrderBinding by viewBinding()

        init {
            binding.orderItemAcceptDescription.isVisible = false
            binding.orderItemAcceptProblemsButton.isVisible = false
            binding.orderItemAcceptButton.text = "Добавить место встречи"
            binding.orderItemDeliveryDateText.text = "Самовывоз"
            binding.orderItemDeliveryDateText.setTextColor(
                ResourcesCompat.getColor(
                    itemView.resources,
                    R.color.black,
                    null
                )
            )
            binding.orderItemDeliveryDate.text = "Доставка"
            binding.orderItemStatusContent.text = "Необходимо добавить место встречи"
            binding.orderItemDeliveryTitle.text = "Адрес:"


            if (isHistory) {
                binding.orderItemStatusImage.setImageResource(R.drawable.ic_fill_checkcircle)
                binding.orderItemStatusImage.imageTintList = null
                binding.orderItemStatusContent.setTextColor(
                    ResourcesCompat.getColor(
                        itemView.resources,
                        R.color.black,
                        null
                    )
                )
                binding.orderItemStatusContent.text = "Сделка завершена"
            }
        }

        fun bind(
            order: Order,
            address: OfferedOrderPlace?,
            user: User?,
            dialog: DialogWrapper?
        ) {
            binding.orderItemName.text = order.cart?.get(0)?.name
            val specialPrice = dialog?.messages?.map{it.priceSuggest ?: it.saleSuggest}?.findLast{it?.status == 1}?.value?.toIntOrNull()
            if(specialPrice != null)
                binding.orderItemPriceText.text = "${specialPrice}Р"
            else
               binding.orderItemPriceText.text = "${order.cart?.get(0)?.priceNew?.toInt()}Р"

            binding.orderItemAcceptButton.setOnClickListener {
                addDealPlace(order.id)
            }
            val buySuggest = dialog?.messages?.map{it.buySuggest}?.findLast{it != null}
                if (buySuggest?.status == 1) {
                    binding.orderItemDeliveryChangeBlock.isVisible = false
                    binding.orderItemAcceptBlock.isVisible = address == null
                    if (address != null) {
                        val timeStamp = address.address.substringAfter(';').toLongOrNull()
                        if (timeStamp != null) {
                            val calendar = Calendar.getInstance()
                                .apply { timeInMillis = timeStamp }
                            val hours = calendar.get(Calendar.HOUR_OF_DAY)
                            val hoursStr = if (hours < 10) "0$hours" else hours.toString()
                            val minutes = calendar.get(Calendar.MINUTE)
                            val minutesStr = if (minutes < 10) "0$minutes" else minutes.toString()
                            val day = calendar.get(Calendar.DAY_OF_MONTH)
                            val dayStr = if (day < 10) "0$day" else day.toString()
                            val month = calendar.get(Calendar.MONTH) + 1
                            val monthStr = if (month < 10) "0$month" else month.toString()
                            binding.orderItemDeliveryContent.text =
                                "${address.address.substringBefore(';')} $dayStr.$monthStr $hoursStr:$minutesStr"
                        } else
                            binding.orderItemDeliveryContent.text = address.address

                        when (address.type) {
                            OfferedPlaceType.PROPOSED.value -> {
                                binding.orderItemDeliveryChangeBlock.isVisible = false
                                binding.orderItemStatusBlock.isVisible = true
                                if (address.status == 0)
                                    binding.orderItemStatusContent.text =
                                        "Покупатель ещё не подтвердил место встречи"
                                else if (isHistory.not())
                                    binding.orderItemStatusContent.text =
                                        "Ожидание встречи с покупателем"
                            }
                            OfferedPlaceType.APPLICATION.value -> {
                                if (address.status == 0) {
                                    binding.orderItemDeliveryChangeBlock.isVisible = true
                                    binding.orderItemDeliveryChangeTitle.text =
                                        "Покупатель предложил место встречи"
                                    binding.orderItemDeliveryChangeContent.isVisible = false
                                    binding.orderItemStatusBlock.isVisible = false
                                    binding.orderItemChangeDeliveryAcceptButton.setOnClickListener {
                                        acceptDealPlace(
                                            OfferedOrderPlaceChange(
                                                idOrder = order.id,
                                                idAddress = address.id,
                                                address = address.address,
                                                status = 1
                                            )
                                        )
                                    }
                                    binding.orderItemChangeDeliveryDeclineButton.setOnClickListener {
                                        user?.let {
                                            declineDealPlace(it.id, order.cart!![0].id)
                                        }
                                    }
                                } else if (isHistory.not())
                                    binding.orderItemStatusContent.text =
                                        "Ожидание встречи с покупателем"
                            }
                        }
                    } else
                        binding.orderItemDeliveryContent.text = "по желанию продавца"
                }else{
                    binding.orderItemDeliveryContent.text = "по желанию продавца"
                    binding.orderItemAcceptBlock.isVisible = false
                    binding.orderItemDeliveryChangeBlock.isVisible = true
                    binding.orderItemDeliveryChangeTitle.text =
                        "Необходимо подтвердить наличие товара"
                    binding.orderItemDeliveryChangeContent.isVisible = false
                    binding.orderItemStatusBlock.isVisible = false
                    binding.orderItemChangeDeliveryAcceptButton.text = "Подтвердить"
                    binding.orderItemChangeDeliveryDeclineButton.text = "Отклонить"
                    binding.orderItemChangeDeliveryAcceptButton.setOnClickListener {
                        if(buySuggest != null)
                        acceptDeal(ChatFunctionInfo(dialogId = dialog.dialog.id, suggest = buySuggest.id, value = order.cart!!.first().priceNew!!.toInt()))
                    }
                    binding.orderItemChangeDeliveryDeclineButton.setOnClickListener {
                        if(buySuggest != null)
                            cancelDeal(ChatFunctionInfo(dialogId = dialog.dialog.id, suggest = buySuggest.id))
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
        return SaleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val address = addresses.find { sales[position].id == it.idOrder }
        val dialog = dialogs.find { it.product?.id == sales[position].cart?.first()?.id }
        holder.bind(sales[position], address, users[position], dialog)
    }

    override fun getItemCount(): Int {
        return sales.size
    }

    fun updateList(
        newList: List<Order>,
        newAddresses: List<OfferedOrderPlace>,
        newAvatars: List<User?>,
        newDialogs: List<DialogWrapper>
    ) {
        sales = newList
        addresses = newAddresses
        users = newAvatars
        dialogs = newDialogs
        notifyDataSetChanged()
    }
}