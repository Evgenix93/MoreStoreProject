package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCreateProductElementBinding

class ForWhoCreateProductAdapter(val onClick: (Int) -> Unit): RecyclerView.Adapter<ForWhoCreateProductAdapter.ForWhoViewHolder>() {

    class ForWhoViewHolder(view: View, onClick: (Int) -> Unit): RecyclerView.ViewHolder(view){
        private val binding: ItemCreateProductElementBinding by viewBinding()

        init {
            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }

        fun bind(){
            binding.view36.isVisible = adapterPosition == 0
            when(adapterPosition){
                0 -> binding.elementNameTextView.text = "Для неё"
                1 -> binding.elementNameTextView.text = "Для него"
                2 -> binding.elementNameTextView.text = "Для детей"

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForWhoViewHolder {
        return ForWhoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_create_product_element, parent, false)) { position ->
            onClick(position)

        }

    }

    override fun onBindViewHolder(holder: ForWhoViewHolder, position: Int) {
        holder.bind()


    }

    override fun getItemCount(): Int {
        return 3

    }
}