package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCreateProductElementBinding
import com.project.morestore.models.Property

class ShoosTypeCreateProductAdapter(val onClick: (Property) -> Unit): RecyclerView.Adapter<ShoosTypeCreateProductAdapter.ShoosTypeViewHolder>() {
    private var list = listOf<Property>()

    class ShoosTypeViewHolder(view: View, onClick: (Int) -> Unit): RecyclerView.ViewHolder(view){
        private val binding: ItemCreateProductElementBinding by viewBinding()

        init {
            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }

        fun bind(property: Property){
            binding.view36.isVisible = adapterPosition == 0
            binding.elementNameTextView.text = property.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoosTypeViewHolder {
        return ShoosTypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_create_product_element, parent, false)){ position ->
            onClick(list[position])

        }

    }

    override fun onBindViewHolder(holder: ShoosTypeViewHolder, position: Int) {
        holder.bind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size

    }

    fun updateList(newList: List<Property>){
        list = newList
        notifyDataSetChanged()
    }
}