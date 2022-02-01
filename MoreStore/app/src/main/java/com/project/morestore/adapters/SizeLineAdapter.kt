package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemShoesSizeLineBinding
import com.project.morestore.databinding.ItemSizeLineBinding
import com.project.morestore.models.SizeLine

class SizeLineAdapter(val isShoos: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list = listOf<SizeLine>()
    private val chosenSizes = mutableListOf<SizeLine>()


    class SizeLineViewHolder(
        view: View,
        val onCheckBoxClicked: (isChecked: Boolean, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val binding: ItemSizeLineBinding by viewBinding()

        //private val binding2: ItemShoesSizeLineBinding by viewBinding()
        fun bind(size: SizeLine, otherSize: Boolean) {
            binding.INTTextView.text = size.int
            binding.ITRUFRTextView.text = size.itRuFr
            binding.WTextView.text = size.w
            binding.USTextView.text = size.us
            binding.UKTextView.text = size.uk
            binding.sizeCheckBox.isChecked = size.isSelected

            binding.sizeCheckBox.setOnClickListener {
                onCheckBoxClicked(binding.sizeCheckBox.isChecked, adapterPosition)
            }
            if (adapterPosition == 0) {
                binding.view8.isVisible = true
            }

            if (otherSize) {
                binding.INTTextView.isVisible = false
                binding.ITRUFRTextView.isVisible = false
                binding.WTextView.isVisible = false
                binding.USTextView.isVisible = false
                binding.UKTextView.isVisible = false
                binding.otherSizeTextView.isVisible = true
                binding.sizeCheckBox.isChecked = size.isSelected
            }

        }

    }

    class ShoosSizeLineViewHolder(
        view: View, val onCheckBoxClicked: (isChecked: Boolean, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemShoesSizeLineBinding by viewBinding()

        fun bind(size: SizeLine, otherSize: Boolean) {
            binding.ITRUFRTextView.text = size.itRuFr
            binding.WTextView.text = size.int
            binding.USTextView.text = size.us
            binding.UKTextView.text = size.uk
            binding.sizeCheckBox.isChecked = size.isSelected

            binding.sizeCheckBox.setOnClickListener {
                onCheckBoxClicked(binding.sizeCheckBox.isChecked, adapterPosition)
            }
            if (adapterPosition == 0) {
                binding.view8.isVisible = true
            }

            if (otherSize) {
                binding.ITRUFRTextView.isVisible = false
                binding.WTextView.isVisible = false
                binding.USTextView.isVisible = false
                binding.UKTextView.isVisible = false
                binding.otherSizeTextView.isVisible = true
                binding.sizeCheckBox.isChecked = size.isSelected
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1) {
            SizeLineViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_size_line,
                    parent,
                    false
                )
            ) { isChecked, position ->
                list[position].isSelected = isChecked


            }
        }else{
            ShoosSizeLineViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_shoes_size_line, parent, false)){
                isChecked, position ->
                list[position].isSelected = isChecked
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is SizeLineViewHolder)
        holder.bind(list[position], position == list.size - 1)
        else (holder as ShoosSizeLineViewHolder).bind(list[position], position == list.size - 1)

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun getItemViewType(position: Int): Int {
        return if(!isShoos){
            1
        }else{
            2
        }
    }

    fun updateList(newList: List<SizeLine>) {
        list = newList
        notifyDataSetChanged()
    }

    fun getChosenSizes(): List<SizeLine> {
        return list
    }
}