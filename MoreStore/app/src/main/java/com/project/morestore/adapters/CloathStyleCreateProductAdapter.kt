package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCreateProductElementBinding

class CloathStyleCreateProductAdapter(val onClick: () -> Unit): RecyclerView.Adapter<CloathStyleCreateProductAdapter.StyleViewHolder>() {



        class StyleViewHolder(view: View, onClick: (Int) -> Unit): RecyclerView.ViewHolder(view){
            private val binding: ItemCreateProductElementBinding by viewBinding()

            init {
                itemView.setOnClickListener {
                    onClick(adapterPosition)
                }
            }

            fun bind(){
                binding.view36.isVisible = adapterPosition == 0
                when(adapterPosition){
                    0 -> binding.elementNameTextView.text = "Зауженные джинсы"
                    1 -> binding.elementNameTextView.text = "Прямые джинсы"
                    2 -> binding.elementNameTextView.text = "Укороченные джинсы"
                    3 -> binding.elementNameTextView.text = "Другое"

                }

            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleViewHolder {
            return StyleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_create_product_element, parent, false)){
                onClick()

            }

        }

        override fun onBindViewHolder(holder: StyleViewHolder, position: Int) {
            holder.bind()


        }

        override fun getItemCount(): Int {
            return 4

        }
    }
