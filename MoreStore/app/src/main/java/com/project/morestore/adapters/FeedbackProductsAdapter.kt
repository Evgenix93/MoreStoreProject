package com.project.morestore.adapters

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemFeedbackProductBinding
import com.project.morestore.models.FeedbackProduct
import com.project.morestore.util.inflater

class FeedbackProductsAdapter(
    private val callback :(FeedbackProduct) -> Unit
) :RecyclerView.Adapter<FeedbackProductsAdapter.FeedbackProductHolder>() {
    private var items = listOf<FeedbackProduct>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackProductHolder {
        return FeedbackProductHolder(ItemFeedbackProductBinding.inflate(parent.inflater, parent, false))
            .apply { itemView.setOnClickListener { callback(product) } }
    }

    override fun onBindViewHolder(holder: FeedbackProductHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    override fun onViewRecycled(holder: FeedbackProductHolder) {
        super.onViewRecycled(holder)
        holder.clear()
    }

    fun setItems(newItems :List<FeedbackProduct>){
        items = newItems
        notifyDataSetChanged()
    }

    class FeedbackProductHolder(
        val views :ItemFeedbackProductBinding
    ) :RecyclerView.ViewHolder(views.root){
        private val ctx = itemView.context
        init {
            views.oldPrice.paintFlags =views.oldPrice.paintFlags or STRIKE_THRU_TEXT_FLAG
        }

        lateinit var product :FeedbackProduct
        fun bind(product :FeedbackProduct){
            this.product = product
            with(views) {
                Glide.with(views.root)
                    .load(product.photos?.firstOrNull()?.photo)
                    .centerCrop()
                    .into(views.photo)
                title.text = product.title
                description.text = desc
                newPrice.text = ctx.getString(R.string.pattern_price, String.format("%,.2f", product.newPrice))
                oldPrice.text = ctx.getString(R.string.pattern_price, String.format("%,.2f", product.oldPrice))
            }
        }

        fun clear() = Glide.with(views.root).clear(views.photo)

        private val desc :String get() {
            val brand = product.brand?.name
            val state = product.state
            brand?.let {
                return ctx.getString(R.string.pattern_dot_divider, it, state)
            }
            return state ?: ""//todo implement skip
        }
    }
}