package com.project.morestore.adapters

import android.graphics.Paint
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemProductBinding
import com.project.morestore.models.Product

class ProductAdapter(val count: Int?, val onClick: (product: Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var list = listOf<Product>()

    class ProductViewHolder(view: View, onClick: (position: Int) -> Unit) : RecyclerView.ViewHolder(view) {
        private val binding: ItemProductBinding by viewBinding()
        init {
            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }
        fun bind(product: Product){
            Log.d("mylog", "bind")
            val crossedStr = "${product.price} ₽".toSpannable().apply { setSpan(StrikethroughSpan(), 0, length ,0) }
            binding.productOldPriceTextView.text = crossedStr
            binding.likesCountTextView.text = product.statistic.wishlist.total.toString()
            binding.productNameTextView.text = product.name
            binding.productPriceTextView.text = "${product.sale} ₽"
            binding.productBrandTextView.text = product.brand.name
            binding.productConditionTextView.text = product.property.find { it.name == "Состояние" }?.value

            Glide.with(itemView)
                .load(product.photo[0].photo)
                .into(binding.productImageView)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        ){ position ->
            onClick(list[position])
        }

    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
       if(position < list.size) holder.bind(list[position])

    }

    override fun getItemCount(): Int {
        return count ?: list.size

    }

    fun updateList(newList: List<Product>){
        list = newList
        notifyDataSetChanged()
    }

}