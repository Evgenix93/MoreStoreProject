package com.project.morestore.adapters

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.*
import androidx.core.content.ContextCompat
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
import com.project.morestore.widgets.ChatMedia

class MessagesAdapter(
    private val acceptDealCallback :() -> Unit,
    private val cancelDealCallback :() -> Unit,
    private val showMediaCallback :(Array<Media>) -> Unit
) :RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     var avatarUri :String = ""
    private var items = listOf<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            Type.DIVIDER.ordinal -> DividerHolder(ChatItemDividerBinding.inflate(parent.inflater, parent, false))
            Type.MY.ordinal -> MyMessageHolder(ChatItemMessageGreenBinding.inflate(parent.inflater, parent, false))
            Type.COMPANION.ordinal -> CompanionMessageHolder(parent.context)
            Type.MY_MEDIA.ordinal -> MyMediaMessageHolder(ChatItemMymediaBinding.inflate(parent.inflater, parent, false))
            Type.DEAL_REQUEST.ordinal -> DealRequestHolder(ChatItemDealRequestBinding.inflate(parent.inflater, parent, false))
            Type.DEAL_ACCEPT.ordinal -> DealAcceptHolder(ChatItemDealAcceptBinding.inflate(parent.inflater, parent, false))
            Type.DEAL_DETAILS.ordinal -> DealDetailsHolder(ChatItemDealDetailsBinding.inflate(parent.inflater, parent, false))
            Type.BUY_REQUEST.ordinal -> BuyRequestHolder(ChatItemBuyRequestBinding.inflate(parent.inflater, parent, false))
            Type.BUY_DETAILS.ordinal -> BuyDetailsHolder(ChatItemDealDetailsBinding.inflate(parent.inflater, parent, false))
            Type.GEO_DETAILS.ordinal -> GeoDetailsHolder(ChatItemDealDetailsBinding.inflate(parent.inflater, parent, false))
            Type.PRICE_REQUEST.ordinal -> PriceRequestHolder(ChatItemPricerequestBinding.inflate(parent.inflater, parent, false))
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
            Type.DEAL_DETAILS.ordinal -> (holder as DealDetailsHolder).bind(item as Message.Special.DealDetails)
            Type.BUY_REQUEST.ordinal -> (holder as BuyRequestHolder).bind(item as Message.Special.BuyRequest)
            Type.BUY_DETAILS.ordinal -> (holder as BuyDetailsHolder).bind(item as Message.Special.BuyDetails)
            Type.GEO_DETAILS.ordinal -> (holder as GeoDetailsHolder).bind(item as Message.Special.GeoDetails)
            Type.PRICE_REQUEST.ordinal -> (holder as PriceRequestHolder).bind(item as Message.Special.PriceRequest)
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
            is Message.Special.DealDetails -> Type.DEAL_DETAILS.ordinal
            is Message.Special.BuyRequest -> Type.BUY_REQUEST.ordinal
            is Message.Special.BuyDetails -> Type.BUY_DETAILS.ordinal
            is Message.Special.GeoDetails -> Type.GEO_DETAILS.ordinal
            is Message.Special.PriceRequest -> Type.PRICE_REQUEST.ordinal
            is Message.Special.PriceAccepted -> Type.PRICE_ACCEPTED.ordinal
        }
    }

    fun setItems(newItems :List<Message>){
        items = newItems
        notifyDataSetChanged()
    }

    fun addMessage(message: Message){
        items = items + listOf(message)
        notifyDataSetChanged()
    }

    enum class Type {
        DIVIDER,
        MY,
        COMPANION,
        MY_MEDIA,
        DEAL_REQUEST,
        DEAL_ACCEPT,
        DEAL_DETAILS,
        BUY_REQUEST,
        BUY_DETAILS,
        GEO_DETAILS,
        PRICE_REQUEST,
        PRICE_ACCEPTED
    }

    inner class DividerHolder(
        private val views :ChatItemDividerBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(divider :Message.Divider){
            views.title.setText(divider.stringId)
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
                views.message.isVisible = true
                views.message.text = message.message
            }
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
            views.acceptDeal.setOnClickListener { acceptDealCallback() }
            views.cancel.setOnClickListener { cancelDealCallback() }
        }
        fun bind(buyRequest :Message.Special.DealRequest){
            with(views){
                avatar.clipToOutline = true
                //avatar.setImageResource(avatarUri)
                time.text = buyRequest.time
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
            }
        }
    }

    inner class PriceAcceptedHolder(
        private val views :ChatItemPriceacceptedBinding
    ) :RecyclerView.ViewHolder(views.root){
        fun bind(priceAccepted :Message.Special.PriceAccepted){
            val ctx = views.root.context
            views.root.text = ctx.getString(R.string.pattern_price, priceAccepted.newPrice)
        }
    }
}