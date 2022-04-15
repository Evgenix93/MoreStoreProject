package com.project.morestore.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.*
import com.project.morestore.models.Media
import com.project.morestore.models.Message
import com.project.morestore.util.createRect
import com.project.morestore.util.dp
import com.project.morestore.util.inflater
import com.project.morestore.util.setStartDrawable
import com.project.morestore.widgets.ChatMedia

class MessagesAdapter(
    private val acceptDealCallback :(Message) -> Unit,
    private val cancelDealCallback :(Message) -> Unit,
    private val submitPriceCallback: (Message) -> Unit,
    private val cancelPriceCallback: (Message) -> Unit,
    private val showMediaCallback :(Array<Media>) -> Unit
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     var avatarUri :String = ""
    private var items = mutableListOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            Type.DIVIDER.ordinal -> DividerHolder(ChatItemDividerBinding.inflate(parent.inflater, parent, false))
            Type.MY.ordinal -> MyMessageHolder(ChatItemMessageGreenBinding.inflate(parent.inflater, parent, false))
            Type.COMPANION.ordinal -> CompanionMessageHolder(parent.context)
            Type.MY_MEDIA.ordinal -> MyMediaMessageHolder(ChatItemMymediaBinding.inflate(parent.inflater, parent, false))
            Type.DEAL_REQUEST.ordinal -> DealRequestHolder(ChatItemDealRequestBinding.inflate(parent.inflater, parent, false))
            Type.DEAL_ACCEPT.ordinal -> DealAcceptHolder(ChatItemDealAcceptBinding.inflate(parent.inflater, parent, false))
            Type.DEAL_CANCEL.ordinal -> DealCancelHolder(ChatItemDealAcceptBinding.inflate(parent.inflater, parent, false))
            Type.DEAL_DETAILS.ordinal -> DealDetailsHolder(ChatItemDealDetailsBinding.inflate(parent.inflater, parent, false))
            Type.BUY_REQUEST.ordinal -> BuyRequestHolder(ChatItemBuyRequestBinding.inflate(parent.inflater, parent, false))
            Type.BUY_DETAILS.ordinal -> BuyDetailsHolder(ChatItemDealDetailsBinding.inflate(parent.inflater, parent, false))
            Type.GEO_DETAILS.ordinal -> GeoDetailsHolder(ChatItemDealDetailsBinding.inflate(parent.inflater, parent, false))
            Type.PRICE_REQUEST.ordinal -> PriceRequestHolder(ChatItemPricerequestBinding.inflate(parent.inflater, parent, false))
            Type.PRICE_SUBMIT.ordinal -> PriceSubmitHolder(ChatItemDealRequestBinding.inflate(parent.inflater, parent, false))
            Type.PRICE_SUBMITTED.ordinal -> PriceSubmittedHolder(ChatItemPricesubmittedBinding.inflate(parent.inflater, parent, false))
            Type.PRICE_CANCELED.ordinal -> PriceCanceledHolder(ChatItemPricesubmittedBinding.inflate(parent.inflater, parent, false))
            Type.PRICE_ACCEPTED.ordinal -> PriceAcceptedHolder(ChatItemPriceacceptedBinding.inflate(parent.inflater, parent, false))
            else -> throw IllegalStateException("unknown type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when(getItemViewType(position)){
            Type.DIVIDER.ordinal -> (holder as DividerHolder).bind(item as Message.Divider)
            Type.MY.ordinal -> (holder as MyMessageHolder).bind(item as Message.My)
            Type.COMPANION.ordinal -> (holder as CompanionMessageHolder).bind(item as Message.Companion)
            Type.MY_MEDIA.ordinal -> (holder as MyMediaMessageHolder).bind(item as Message.MyMedia)
            Type.DEAL_REQUEST.ordinal -> (holder as DealRequestHolder).bind(item as Message.Special.DealRequest)
            Type.DEAL_ACCEPT.ordinal -> (holder as DealAcceptHolder).bind(item as Message.Special.DealAccept)
            Type.DEAL_CANCEL.ordinal -> (holder as DealCancelHolder).bind(item as Message.Special.DealCancel)
            Type.DEAL_DETAILS.ordinal -> (holder as DealDetailsHolder).bind(item as Message.Special.DealDetails)
            Type.BUY_REQUEST.ordinal -> (holder as BuyRequestHolder).bind(item as Message.Special.BuyRequest)
            Type.BUY_DETAILS.ordinal -> (holder as BuyDetailsHolder).bind(item as Message.Special.BuyDetails)
            Type.GEO_DETAILS.ordinal -> (holder as GeoDetailsHolder).bind(item as Message.Special.GeoDetails)
            Type.PRICE_REQUEST.ordinal -> (holder as PriceRequestHolder).bind(item as Message.Special.PriceRequest)
            Type.PRICE_SUBMIT.ordinal -> (holder as PriceSubmitHolder).bind(item as Message.Special.PriceSubmit)
            Type.PRICE_SUBMITTED.ordinal -> (holder as PriceSubmittedHolder).bind(item as Message.Special.PriceSubmitted)
            Type.PRICE_CANCELED.ordinal -> (holder as PriceCanceledHolder).bind(item as Message.Special.PriceCanceled)
            Type.PRICE_ACCEPTED.ordinal -> (holder as PriceAcceptedHolder).bind(item as Message.Special.PriceAccepted)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){//todo move to delegates
            is Message.Divider -> Type.DIVIDER.ordinal
            is Message.My -> Type.MY.ordinal
            is Message.Companion -> Type.COMPANION.ordinal
            is Message.MyMedia -> Type.MY_MEDIA.ordinal
            is Message.Special.DealRequest -> Type.DEAL_REQUEST.ordinal
            is Message.Special.DealAccept -> Type.DEAL_ACCEPT.ordinal
            is Message.Special.DealCancel -> Type.DEAL_CANCEL.ordinal
            is Message.Special.DealDetails -> Type.DEAL_DETAILS.ordinal
            is Message.Special.BuyRequest -> Type.BUY_REQUEST.ordinal
            is Message.Special.BuyDetails -> Type.BUY_DETAILS.ordinal
            is Message.Special.GeoDetails -> Type.GEO_DETAILS.ordinal
            is Message.Special.PriceRequest -> Type.PRICE_REQUEST.ordinal
            is Message.Special.PriceSubmit -> Type.PRICE_SUBMIT.ordinal
            is Message.Special.PriceSubmitted -> Type.PRICE_SUBMITTED.ordinal
            is Message.Special.PriceCanceled -> Type.PRICE_CANCELED.ordinal
            is Message.Special.PriceAccepted -> Type.PRICE_ACCEPTED.ordinal
        }
    }

    fun setItems(newItems :List<Message>){
        items = newItems.toMutableList()
        notifyDataSetChanged()
    }

    fun addMessage(message: Message){
        items = (items + listOf(message)).toMutableList()
        notifyDataSetChanged()
    }

    fun isTodayMessages(): Boolean{
        return items.find { it is Message.Divider && it.text == "сегодня" } != null
    }

    enum class Type {
        DIVIDER,
        MY,
        COMPANION,
        MY_MEDIA,
        DEAL_REQUEST,
        DEAL_ACCEPT,
        DEAL_CANCEL,
        DEAL_DETAILS,
        BUY_REQUEST,
        BUY_DETAILS,
        GEO_DETAILS,
        PRICE_REQUEST,
        PRICE_SUBMIT,
        PRICE_SUBMITTED,
        PRICE_CANCELED,
        PRICE_ACCEPTED
    }

    inner class DividerHolder(
        private val views :ChatItemDividerBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(divider :Message.Divider){
            views.title.text = divider.text
        }
    }

    inner class MyMessageHolder(
        private val views :ChatItemMessageGreenBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(message :Message.My){
            views.time.text = message.time
            views.status.setImageResource(message.status)
            views.message.text= message.message
        }
    }

    inner class CompanionMessageHolder(
        private val context :Context
    ) :RecyclerView.ViewHolder(LinearLayout(context).apply {
        orientation = VERTICAL
        showDividers = SHOW_DIVIDER_MIDDLE
        dividerDrawable = createRect(0, 8.dp)
    }){
        private val holder = itemView as LinearLayout

        fun bind(message :Message.Companion){
            holder.removeAllViews()
            message.msgs.forEach {
                ChatItemMessageBlueBinding
                    .inflate(context.inflater, holder, true)
                    .apply {
                        if(it == message.msgs.last()){
                           // if(avatarId == R.drawable.ic_headphones) avatar.setPadding(5.dp, 5.dp, 5.dp, 5.dp)
                            avatar.clipToOutline = true
                            if(avatarUri.isEmpty())
                                avatar.setImageResource(R.drawable.ic_headphones)
                            else Glide.with(itemView)
                                .load(avatarUri)
                                .into(avatar)
                        } else {
                            avatar.visibility = INVISIBLE
                        }
                        time.text = it.time
                        this.message.text= it.message
                    }
            }
        }
    }

    inner class MyMediaMessageHolder(
        private val views :ChatItemMymediaBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(message :Message.MyMedia){
            views.time.text = message.time
            views.status.setImageResource(message.status)
            views.media.removeAllViews()
            if(message.message != null){
                views.messageLinearLayout.isVisible = true
                views.message.text = message.message
            }else views.messageLinearLayout.isVisible = false
            for ((count, media) in message.media.withIndex()){
                ChatMedia(views.root.context, media, message.media.size)
                    .apply {
                        (layoutParams as FrameLayout.LayoutParams).apply {
                            setMargins(8.dp, 0, 0, 0)
                        }
                        setOnClickListener { showMediaCallback(message.media) }
                    }
                    .also {
                        if(count < 4)
                        views.media.addView(it) }
            }
        }
    }

    inner class DealRequestHolder(
        private val views :ChatItemDealRequestBinding
    ) :RecyclerView.ViewHolder(views.root){
        init {

        }
        fun bind(buyRequest :Message.Special.DealRequest){
            with(views){
                avatar.clipToOutline = true
               // avatar.setImageResource(avatarUri)
                time.text = buyRequest.time
                acceptDeal.setOnClickListener {
                    items[adapterPosition] = Message.Special.DealAccept("13:00")
                    notifyItemChanged(adapterPosition)
                    acceptDealCallback(buyRequest)
                }
                cancel.setOnClickListener {
                    items[adapterPosition] = Message.Special.DealCancel("13:00")
                    notifyItemChanged(adapterPosition)
                    cancelDealCallback(buyRequest)
                }
            }
        }
    }

    inner class DealAcceptHolder(
        private val views :ChatItemDealAcceptBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(item :Message.Special.DealAccept){
            with(views){
                avatar.clipToOutline = true
                //avatar.setImageResource(avatarUri)
                time.text = item.time
            }
        }
    }

    inner class DealCancelHolder(
        private val views :ChatItemDealAcceptBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(item :Message.Special.DealCancel){
            with(views){
                avatar.clipToOutline = true
                time.text = item.time
                subtitle.text = "Отменено"
                icon.setImageResource(R.drawable.ic_x)
            }
        }
    }

    inner class DealDetailsHolder(
        private val views :ChatItemDealDetailsBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(item :Message.Special.DealDetails){
        }
    }

    inner class BuyRequestHolder(
        private val views :ChatItemBuyRequestBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(buyRequest :Message.Special.BuyRequest){
            with(views){
                time.text = buyRequest.time
                status.setImageResource(buyRequest.statusIcon)
                subtitle.text = buyRequest.text
                subtitle.setTextColor(buyRequest.textColor)
                views.icon.setImageResource(buyRequest.submitStatus)
            }
        }
    }

    inner class BuyDetailsHolder(
        private val views :ChatItemDealDetailsBinding
    ) :RecyclerView.ViewHolder(views.root){
        init {
            views.details.setText(R.string.fillData)
        }
        fun bind(item :Message.Special.BuyDetails){
        }
    }

    inner class GeoDetailsHolder(
        private val views :ChatItemDealDetailsBinding
    ) :RecyclerView.ViewHolder(views.root){
        init {
            views.details.setText(R.string.addressAndPlaceOfDeal)
            views.details.setTextColor(ContextCompat.getColor(views.root.context, R.color.black))
            views.icon.setImageResource(R.drawable.ic_geopoint)
            views.title.setText(R.string.suggestPlaceDeal)
        }
        fun bind(item :Message.Special.GeoDetails){

        }
    }

    inner class PriceRequestHolder(
        private val views :ChatItemPricerequestBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(priceRequest :Message.Special.PriceRequest){
            val ctx = views.root.context
            with(views){
                time.text = priceRequest.time
                status.setImageResource(priceRequest.status)
                price.text = ctx.getString(R.string.pattern_price, priceRequest.newPrice)
                requestStatus.text = priceRequest.text
                requestStatus.setTextColor(priceRequest.textColor)


            }
        }
    }

    inner class PriceSubmitHolder(
        private val views :ChatItemDealRequestBinding
    ) :RecyclerView.ViewHolder(views.root){
        init {

        }
        fun bind(priceSubmit :Message.Special.PriceSubmit){
            with(views){
                avatar.clipToOutline = true
                //avatar.setImageResource(avatarUri)
                title.text = "Своя цена"
                icon.setImageResource(R.drawable.ic_coins)
                icon.imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(itemView.resources, R.color.blue4, null))
                time.text = priceSubmit.time
                acceptDeal.text = "Одобрить цену"
                acceptDeal.setOnClickListener {
                    items[adapterPosition] = Message.Special.PriceSubmitted("14:00", priceSubmit.newPrice)
                    items.add(Message.Special.PriceAccepted(priceSubmit.newPrice))
                    notifyItemRangeChanged(adapterPosition, 2)
                    submitPriceCallback(priceSubmit)
                }
                cancel.setOnClickListener {
                    items[adapterPosition] = Message.Special.PriceCanceled("14:00", priceSubmit.newPrice)
                    notifyItemChanged(adapterPosition)
                    cancelPriceCallback(priceSubmit)
                }
            }
        }
    }

    inner class PriceSubmittedHolder(
        private val views :ChatItemPricesubmittedBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(priceRequest :Message.Special.PriceSubmitted){
            val ctx = views.root.context
            with(views){
                time.text = priceRequest.time
                price.text = ctx.getString(R.string.pattern_price, priceRequest.newPrice)
            }
        }
    }

    inner class PriceCanceledHolder(
        private val views :ChatItemPricesubmittedBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(priceRequest :Message.Special.PriceCanceled){
            val ctx = views.root.context
            with(views){
                time.text = priceRequest.time
                requestStatus.text = "Отменено"
                requestStatus.setStartDrawable(ResourcesCompat.getDrawable(itemView.resources, R.drawable.ic_x, null)!!)
                price.text = ctx.getString(R.string.pattern_price, priceRequest.newPrice)

            }
        }
    }

    inner class PriceAcceptedHolder(
        private val views :ChatItemPriceacceptedBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(priceAccepted :Message.Special.PriceAccepted){
            val ctx = views.root.context
            views.root.text = "Цена снижена до ${priceAccepted.newPrice}₽"//ctx.getString(R.string.pattern_price, priceAccepted.newPrice)
        }
    }
}