package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemSizeCardBinding
import com.project.morestore.models.Size
import com.project.morestore.util.SizeCard

class SizeCardsAdapter : RecyclerView.Adapter<SizeCardsAdapter.SizeCardViewHolder>() {
    private var list = listOf<Size>()
    private val chosenSizes = mutableListOf<Size>()


    class SizeCardViewHolder(view: View, onClick: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val binding: ItemSizeCardBinding by viewBinding()

        init {
            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }

        fun bind(item: Size) {
            binding.sizeNameTextView.text = item.name
            binding.root.apply {
                strokeColor = if (item.chosen == true) {
                    this.setBackgroundColor(resources.getColor(R.color.gray3))
                    resources.getColor(R.color.green)
                } else {
                    this.setBackgroundColor(resources.getColor(R.color.white))
                    resources.getColor(R.color.gray1)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeCardViewHolder {
        return SizeCardViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_size_card, parent, false)
        ) { position ->
            if (list[position].chosen == true) {
                list[position].chosen = false
                notifyItemChanged(position)
                chosenSizes.remove(list[position])
            } else {
                if (chosenSizes.size < 3) {
                    list[position].chosen = true
                    notifyItemChanged(position)
                    chosenSizes.add(list[position])
                }
            }
        }

    }

    override fun onBindViewHolder(holder: SizeCardViewHolder, position: Int) {
        holder.bind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size

    }

    fun updateList(newList: List<Size>) {
        list = newList
        notifyDataSetChanged()

    }

    fun getChosenSizes(): List<Size> {
        return chosenSizes
    }
}