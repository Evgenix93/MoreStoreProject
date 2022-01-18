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
import com.project.morestore.R
import com.project.morestore.databinding.ItemProductBinding

class ProductAdapter(val count: Int) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding: ItemProductBinding by viewBinding()
        fun bind(){
            Log.d("mylog", "bind")
            val crossedStr = binding.productOldPriceTextView.text.toSpannable().apply { setSpan(StrikethroughSpan(), 0, binding.productOldPriceTextView.text.length ,0) }
            binding.productOldPriceTextView.text = crossedStr

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind()

    }

    override fun getItemCount(): Int {
        return count

    }

}