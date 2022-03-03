package com.project.morestore.adapters

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.databinding.ItemFeedbackProductBinding
import com.project.morestore.models.FeedbackProduct
import com.project.morestore.util.inflater

class FeedbackProductsAdapter(
    private val callback :(FeedbackProduct) -> Unit
) :RecyclerView.Adapter<FeedbackProductsAdapter.FeedbackProductHolder>() {
    private val items = listOf(
        FeedbackProduct(R.drawable.feedback1, "Плащ тренч", "Belanciaga", "Почти новое", 8000, 10600),
        FeedbackProduct(R.drawable.feedback2, "Толстовка", "Belanciaga", "Почти новое", 890, 5600),
        FeedbackProduct(R.drawable.feedback3, "Сапоги salamander 35", "Belanciaga", "Почти новое", 650, 5600),
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackProductHolder {
        return FeedbackProductHolder(ItemFeedbackProductBinding.inflate(parent.inflater, parent, false))
            .apply { itemView.setOnClickListener { callback(product) } }
    }

    override fun onBindViewHolder(holder: FeedbackProductHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class FeedbackProductHolder(
        val views :ItemFeedbackProductBinding
    ) :RecyclerView.ViewHolder(views.root){

        init {
            views.oldPrice.paintFlags =views.oldPrice.paintFlags or STRIKE_THRU_TEXT_FLAG
        }


        lateinit var product :FeedbackProduct
        fun bind(product :FeedbackProduct){
            this.product = product
            val ctx = itemView.context
            with(views) {
                photo.setImageResource(product.photo)
                title.text = product.title
                description.text = ctx.getString(R.string.pattern_dot_divider, product.brand, product.status)
                newPrice.text = ctx.getString(R.string.pattern_price, String.format("%,d", product.newPrice))
                oldPrice.text = ctx.getString(R.string.pattern_price, String.format("%,d", product.oldPrice))
            }
        }
    }
}