package com.project.morestore.adapters

import android.graphics.Paint
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemProductBinding
import com.project.morestore.models.Product

class ProductAdapter(val count: Int?, val onClick: (product: Product) -> Unit) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var list = listOf<Product>()
    private var wishedList = listOf<Product>()

    class ProductViewHolder(view: View, onClick: (position: Int) -> Unit) : RecyclerView.ViewHolder(view) {
        private val binding: ItemProductBinding by viewBinding()
        init {
            itemView.setOnClickListener {
                Log.d("MyDebug", "onClick adapter")
                onClick(adapterPosition)
            }
        }
        fun bind(product: Product){
            Log.d("productbrand", product.brand.toString())
            Log.d("product", product.toString())
            Log.d("mylog", "bind")
            val crossedStr = if(product.price == 0f) "" else "${product.price} ₽".toSpannable().apply { setSpan(StrikethroughSpan(), 0, length ,0) }
            binding.productOldPriceTextView.text = crossedStr
            val likesCount = product.statistic?.wishlist?.total ?: 0
            binding.likesCountTextView.text = likesCount.toString()
            binding.productNameTextView.text = product.name
            binding.productPriceTextView.text = if(product.priceNew == null) "0 ₽" else "${product.priceNew} ₽"
            binding.productBrandTextView.text = if(product.brand == null)
                "Другое" + "• ${product.property?.find { it.name == "Состояние" }?.value.orEmpty()}"
             else product.brand.name + "• ${product.property?.find { it.name == "Состояние" }?.value.orEmpty()}"


            Glide.with(itemView)
                .load(product.photo[0].photo)
                .into(binding.productImageView)

            binding.heartIcon.setImageResource(if(product.wishlist == true) R.drawable.ic_heart_red
            else R.drawable.ic_heart)

            binding.statusInfoCardView.isVisible = false
            binding.statusReadInfoCardView.isVisible = false

            when(product.status){
                1 -> {
                    binding.statusReadInfoCardView.isVisible = false
                    binding.statusInfoCardView.isVisible = false
                    binding.productImageView.alpha = 1f
                }
                6 -> {
                    binding.statusReadInfoCardView.isVisible = false
                    binding.statusInfoCardView.isVisible = true
                    binding.statusInfoCardView.setCardBackgroundColor(ResourcesCompat.getColor(itemView.resources, R.color.green, null))
                    binding.statusTextView.text = "Одобрено"
                    binding.productImageView.alpha = 1f
                }
                7 -> {
                    binding.statusReadInfoCardView.isVisible = false
                    binding.statusInfoCardView.isVisible = true
                    binding.statusInfoCardView.setCardBackgroundColor(ResourcesCompat.getColor(itemView.resources, R.color.orange, null))
                    binding.statusTextView.text = "Забронировано"
                    binding.productImageView.alpha = 1f
                }
                8 -> {
                    binding.statusReadInfoCardView.isVisible = false
                    binding.statusInfoCardView.isVisible = true
                    binding.statusInfoCardView.setCardBackgroundColor(ResourcesCompat.getColor(itemView.resources, R.color.black, null))
                    binding.statusTextView.text = "Уже купили"
                    binding.productImageView.alpha = 1f
                }

            }
                if(product.statusUser?.read == true){
                binding.statusReadInfoCardView.isVisible = true
                binding.productImageView.alpha = 0.65f
            }



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

    fun updateWishedList(newList: List<Product>){
        wishedList = newList
        notifyDataSetChanged()
    }

}