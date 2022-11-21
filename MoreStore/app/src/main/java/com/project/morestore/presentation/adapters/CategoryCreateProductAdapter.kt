package com.project.morestore.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCreateProductElementBinding
import com.project.morestore.data.models.ProductCategory

class CategoryCreateProductAdapter(val onClick: (ProductCategory) -> Unit): RecyclerView.Adapter<CategoryCreateProductAdapter.CategoryViewHolder>() {
    private var list = listOf<ProductCategory>()

    class CategoryViewHolder(view: View, onClick: (Int) -> Unit): RecyclerView.ViewHolder(view){
        private val binding: ItemCreateProductElementBinding by viewBinding()

        init {
            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }

        fun bind(category: ProductCategory){
            binding.elementNameTextView.text = category.name
            binding.topLine.isVisible = adapterPosition == 0
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_create_product_element, parent, false)){ position ->
            onClick(list[position])

        }

    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size

    }

    fun updateList(newList: List<ProductCategory>){
        list = newList
        notifyDataSetChanged()
    }
}