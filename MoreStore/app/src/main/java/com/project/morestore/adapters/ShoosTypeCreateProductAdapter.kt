package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCreateProductElementBinding

class ShoosTypeCreateProductAdapter(val onClick: () -> Unit): RecyclerView.Adapter<ShoosTypeCreateProductAdapter.ShoosTypeViewHolder>() {

    class ShoosTypeViewHolder(view: View, onClick: () -> Unit): RecyclerView.ViewHolder(view){
        private val binding: ItemCreateProductElementBinding by viewBinding()

        init {
            itemView.setOnClickListener {
                onClick()
            }
        }

        fun bind(){
            binding.view36.isVisible = adapterPosition == 0
            when(adapterPosition){
                0 -> binding.elementNameTextView.text = "Ботинки"
                1 -> binding.elementNameTextView.text = "Кроссовки и кеды"
                2 -> binding.elementNameTextView.text = "Классика"
                3 -> binding.elementNameTextView.text = "Пляжная и домашняя обувь"
                4 -> binding.elementNameTextView.text = "Сандали"
                5 -> binding.elementNameTextView.text = "Слипоны"
                6 -> binding.elementNameTextView.text = "Эспадрильи и мокасины"
                7 -> binding.elementNameTextView.text = "Другое"






            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoosTypeViewHolder {
        return ShoosTypeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_create_product_element, parent, false)){
            onClick()

        }

    }

    override fun onBindViewHolder(holder: ShoosTypeViewHolder, position: Int) {
        holder.bind()

    }

    override fun getItemCount(): Int {
        return 8

    }
}