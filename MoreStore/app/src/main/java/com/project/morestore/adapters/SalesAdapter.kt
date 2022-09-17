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
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.models.*
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import java.util.*

class SalesAdapter(
    private val isHistory: Boolean,
    private val addDealPlace: (orderId: Long) -> Unit,
    private val acceptDealPlace: (OfferedOrderPlaceChange) -> Unit,
    private val declineDealPlace: (Long, Long) -> Unit,
    private val acceptDeal:(ChatFunctionInfo) -> Unit,
    private val cancelDeal:(ChatFunctionInfo) -> Unit,
    private val onProfileClick: (User) -> Unit,
    private val onClick: (OrderItem) -> Unit,
    private val onDeliveryCreateClick: (Order) -> Unit
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
                binding.orderItemDeliveryChangeBlock.isVisible = false
                binding.orderItemAcceptBlock.isVisible = false
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
            binding.orderItemDeliveryDateText.text = when (order.delivery) {
                1 -> "самовывоз"
                2 -> "yandex Go"
                3 -> "CDEK"
                else -> ""
            }
            binding.orderItemDeliveryContent.text = when {
                order.delivery == 2 -> order.placeAddress
                order.delivery == 3 -> order.placeAddress
                order.place == 1 && order.delivery == 1 -> address?.address?.substringBefore(";") ?: "по желанию продавца"
                order.place == 2 && order.delivery == 1 -> address?.address?.substringBefore(";")
                else -> ""
            }
            var orderStatus = OrderStatus.NOT_SUBMITTED
            binding.orderItemName.text = order.cart?.get(0)?.name
            //val specialPrice = dialog?.messages?.map{it.priceSuggest ?: it.saleSuggest}?.findLast{it?.status == 1}?.value?.toIntOrNull()
              //  ?: order.cart?.get(0)?.priceNew?.toInt()
            val specialPrice = order.cart?.first()?.statusUser?.price?.value?.toIntOrNull() ?: order.cart?.first()?.statusUser?.sale?.value?.toIntOrNull()
                    ?: order.cart?.first()?.priceNew?.toInt()
            binding.orderItemPriceText.text = "${specialPrice}Р"

            binding.orderItemAcceptButton.setOnClickListener {
                addDealPlace(order.id)
            }
            var date = "-"
            var time = "-"
            val buySuggest = order.cart?.first()?.statusUser?.buy
            if(isHistory.not()) {
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
                            val year = calendar.get(Calendar.YEAR)
                            date = "$dayStr.$monthStr.$year"
                            time = "$hoursStr:$minutesStr"
                            binding.orderItemDeliveryContent.text =
                                "${address.address.substringBefore(';')} $dayStr.$monthStr $hoursStr:$minutesStr"
                        } else
                            binding.orderItemDeliveryContent.text = address.address

                        when (address.type) {
                            OfferedPlaceType.PROPOSED.value -> {
                                binding.orderItemDeliveryChangeBlock.isVisible = false
                                binding.orderItemStatusBlock.isVisible = true
                                if (address.status == 0) {
                                    orderStatus = OrderStatus.MEETING_NOT_ACCEPTED_SELLER
                                    binding.orderItemStatusContent.text =
                                        "Покупатель ещё не подтвердил место встречи"
                                }
                                else {
                                    orderStatus = OrderStatus.RECEIVED_SELLER
                                    binding.orderItemStatusContent.text =
                                        "Ожидание встречи с покупателем"
                                }
                            }
                            OfferedPlaceType.APPLICATION.value -> {
                                if (address.status == 0) {
                                    orderStatus = OrderStatus.CHANGE_MEETING_SELLER
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
                                            declineDealPlace(it.id, order.cart[0].id)
                                        }
                                    }
                                } else {
                                    orderStatus = OrderStatus.RECEIVED_SELLER
                                    binding.orderItemStatusContent.text =
                                        "Ожидание встречи с покупателем"
                                }
                            }
                        }
                    } else {
                        orderStatus = OrderStatus.ADD_MEETING

                    }
                }else if(buySuggest?.status == 0 || buySuggest == null){
                    orderStatus = OrderStatus.NOT_SUBMITTED_SELLER
                    binding.orderItemAcceptBlock.isVisible = false
                    binding.orderItemDeliveryChangeBlock.isVisible = true
                    binding.orderItemDeliveryChangeTitle.text =
                        "Необходимо подтвердить наличие товара"
                    binding.orderItemDeliveryChangeContent.isVisible = false
                    binding.orderItemStatusBlock.isVisible = false
                    binding.orderItemChangeDeliveryAcceptButton.isVisible = true
                    binding.orderItemChangeDeliveryDeclineButton.isVisible = true
                    binding.orderItemChangeDeliveryAcceptButton.text = "Подтвердить"
                    binding.orderItemChangeDeliveryDeclineButton.text = "Отклонить"
                    binding.orderItemChangeDeliveryAcceptButton.isEnabled = true
                    binding.orderItemChangeDeliveryDeclineButton.isEnabled = true
                    binding.orderItemChangeDeliveryAcceptButton.setOnClickListener {
                        if (buySuggest != null) {
                            binding.orderItemChangeDeliveryAcceptButton.isEnabled = false
                            binding.orderItemChangeDeliveryDeclineButton.isEnabled = false
                            acceptDeal(
                                ChatFunctionInfo(
                                    dialogId = dialog!!.dialog.id,
                                    suggest = buySuggest.id,
                                    value = specialPrice
                                )
                            )
                        }
                    }
                    binding.orderItemChangeDeliveryDeclineButton.setOnClickListener {
                        if (buySuggest != null)
                            cancelDeal(
                                ChatFunctionInfo(
                                    dialogId = dialog!!.dialog.id,
                                    suggest = buySuggest.id
                                )
                            )
                    }
                }else{
                    binding.orderItemAcceptBlock.isVisible = false
                    binding.orderItemDeliveryChangeBlock.isVisible = true
                    if(order.cart.first().statusUser?.buy?.idCanceled != order.idSeller) {
                        orderStatus = OrderStatus.DECLINED_BUYER
                        binding.orderItemDeliveryChangeTitle.text = "К сожалению покупатель отклонил сделку"
                    }
                   else {
                        orderStatus = OrderStatus.DECLINED
                        binding.orderItemDeliveryChangeTitle.text =
                            "Сделка отклонена"
                    }
                    binding.orderItemDeliveryChangeContent.isVisible = false
                    binding.orderItemStatusBlock.isVisible = false
                    binding.orderItemChangeDeliveryAcceptButton.isVisible = false
                    binding.orderItemChangeDeliveryDeclineButton.isVisible = false

                }
                if(order.pay == 2 && buySuggest?.status != 2){
                    if(!order.isPayment && buySuggest?.status == 1 ){
                        orderStatus = OrderStatus.NOT_PAYED_SELLER
                        binding.orderItemDeliveryChangeBlock.isVisible = false
                        binding.orderItemStatusBlock.isVisible = true
                        binding.orderItemStatusContent.text = "Не оплачено"
                        binding.orderItemAcceptBlock.isVisible = false
                    }
                    if(order.isPayment && buySuggest?.status == 1){
                        if(order.delivery != 1) {
                            if(order.deliveryStatus == null) {
                                orderStatus = OrderStatus.CREATE_DELIVERY
                                binding.orderItemDeliveryChangeBlock.isVisible = false
                                binding.orderItemStatusBlock.isVisible = true
                                binding.orderItemStatusContent.text = "Оплачено"
                                binding.orderItemAcceptBlock.isVisible = true
                                binding.orderItemAcceptButton.text = "Подтвердить доставку"

                                binding.orderItemAcceptButton.setOnClickListener {
                                    onDeliveryCreateClick(order)
                                }
                            }else {
                                binding.orderItemDeliveryChangeBlock.isVisible = false
                                binding.orderItemStatusBlock.isVisible = true
                                binding.orderItemStatusContent.text = order.deliveryStatus
                                binding.orderItemAcceptBlock.isVisible = false
                                binding.orderItemDeliveryContent.text = order.placeAddress
                                if(order.deliveryStatus == "Данные доставки некорректны")
                                    orderStatus = OrderStatus.DELIVERY_STATUS_NOT_VALID
                                else if(order.deliveryStatus == "Неизвестеый статус доставки")
                                    orderStatus = OrderStatus.DELIVERY_STATUS_NOT_DEFINED
                                else
                                    orderStatus = OrderStatus.DELIVERY_STATUS_ACCEPTED

                            }
                        }
                        /*if(order.delivery == 1 && address?.status == 1) {
                            binding.orderItemAcceptBlock.isVisible = false
                            binding.orderItemStatusContent.text = "Оплачено, ожидание встречи с покупателем"
                        }*/



                    }

                }
            }else{
              orderStatus = OrderStatus.RECEIVED_SUCCESSFULLY
            }

            binding.orderItemUserName.text = "${user?.name} ${user?.surname}"
            Glide.with(itemView)
                .load(user?.avatar?.photo)
                .circleCrop()
                .into(binding.orderItemUserIcon)

            Glide.with(itemView)
                .load(order.cart?.get(0)?.photo?.get(0)?.photo)
                .into(binding.orderItemPreview)

            binding.orderItemUserIcon.setOnClickListener {
                if(user != null) onProfileClick(user)
            }
            binding.orderItemUserName.setOnClickListener {
                if(user != null) onProfileClick(user)
            }
            binding.root.setOnClickListener{
              onClick(OrderItem(
                  id = order.id,
                  user = user,
                  photo = order.cart!!.first().photo.first().photo,
                  name = order.cart.first().name,
                  price = specialPrice ?: 0,
                  deliveryDate = date,
                  deliveryInfo = when (order.delivery) {
                      OrderCreateFragment.TAKE_FROM_SELLER -> "Заберу у продавца"
                      OrderCreateFragment.YANDEX_GO -> "yandex"
                      OrderCreateFragment.ANOTHER_CITY -> "СДЕК"
                      else -> ""},
                  status = orderStatus,
                  newAddress = address?.address,
                  newTime = time,
                  sellerId = order.cart.first().idUser!!,
                  productId = order.cart.first().id,
                  newAddressId = address?.id,
                  chatFunctionInfo = if(dialog != null && buySuggest != null)
                      ChatFunctionInfo(
                      dialogId = dialog.dialog.id,
                      suggest = buySuggest.id,
                      value = specialPrice
                  )else null,
                  offeredOrderPlace = address,
                  product = order.cart.first(),
                  buyerId = order.idUser,
                  cdekYandexAddress = order.placeAddress,
                  deliveryStatusInfo = order.deliveryStatus
              ))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        return SaleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val address = addresses.find { sales[position].id == it.idOrder }
        val dialog = dialogs.find { it.product?.id == sales[position].cart?.first()?.id &&
        it.dialog.user.id == sales[position].idUser}
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