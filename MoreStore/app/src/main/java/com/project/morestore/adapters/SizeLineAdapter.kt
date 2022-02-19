package com.project.morestore.adapters

import android.util.Log
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

class SizeLineAdapter(val isShoos: Boolean, val isCreateProduct: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list = listOf<SizeLine>()
    private var bottomSizeList: List<SizeLine>? = null
    private val chosenSizes = mutableListOf<SizeLine>()


    class SizeLineViewHolder(
        view: View,
        val isCreateProduct: Boolean = false,
        val onCheckBoxClicked: (isChecked: Boolean, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val binding: ItemSizeLineBinding by viewBinding()

        //private val binding2: ItemShoesSizeLineBinding by viewBinding()
        fun bind(size: SizeLine, otherSize: Boolean) {
            Log.d("bind", otherSize.toString())

            binding.INTTextView.text = size.int
            binding.ITRUFRTextView.text = size.itRuFr
            binding.WTextView.text = size.w
            binding.USTextView.text = size.us
            binding.UKTextView.text = size.uk
            binding.sizeCheckBox.isChecked = size.isSelected

            binding.sizeCheckBox.setOnClickListener {
                onCheckBoxClicked(binding.sizeCheckBox.isChecked, adapterPosition)
            }

                binding.view8.isVisible = adapterPosition == 0


            binding.INTTextView.isVisible = !otherSize
            binding.ITRUFRTextView.isVisible = !otherSize
            binding.WTextView.isVisible = !otherSize
            binding.USTextView.isVisible = !otherSize
            binding.UKTextView.isVisible = !otherSize
            binding.otherSizeTextView.isVisible = otherSize
            binding.sizeCheckBox.isChecked = size.isSelected


            binding.sizeCheckBox.isVisible = !isCreateProduct
            binding.checkImageView.isVisible = isCreateProduct
            binding.checkImageView.setOnClickListener {
                //binding.checkImageView.drawable.setTint()
            }


        }

    }

    class ShoosSizeLineViewHolder(
        view: View, val isCreateProduct: Boolean = false, val onCheckBoxClicked: (isChecked: Boolean, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemShoesSizeLineBinding by viewBinding()

        fun bind(size: SizeLine, otherSize: Boolean) {
            Log.d("mytest", size.toString())
            Log.d("bind", otherSize.toString())
            binding.ITRUFRTextView.text = size.itRuFr
            binding.WTextView.text = size.int
            binding.USTextView.text = size.us
            binding.UKTextView.text = size.uk
            binding.sizeCheckBox.isChecked = size.isSelected

            binding.sizeCheckBox.setOnClickListener {
                onCheckBoxClicked(binding.sizeCheckBox.isChecked, adapterPosition)
            }

                binding.view8.isVisible = adapterPosition == 0



            binding.ITRUFRTextView.isVisible = !otherSize
            binding.WTextView.isVisible = !otherSize
            binding.USTextView.isVisible = !otherSize
            binding.UKTextView.isVisible = !otherSize
            binding.otherSizeTextView.isVisible = otherSize
            binding.sizeCheckBox.isChecked = size.isSelected

            binding.sizeCheckBox.isVisible = !isCreateProduct
            binding.checkImageView.isVisible = isCreateProduct





        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            SizeLineViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_size_line,
                    parent,
                    false
                ),
                isCreateProduct = isCreateProduct
            ) { isChecked, position ->
                list[position].isSelected = isChecked



            }
        } else {
            ShoosSizeLineViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_shoes_size_line, parent, false),
                isCreateProduct = isCreateProduct
            ) { isChecked, position ->
                list[position].isSelected = isChecked
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SizeLineViewHolder)
            holder.bind(list[position], position == list.lastIndex)
        else (holder as ShoosSizeLineViewHolder).bind(list[position], position == list.lastIndex)

    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun getItemViewType(position: Int): Int {
        return if (!isShoos) {
            1
        } else {
            2
        }
    }

    fun updateList(newList: List<SizeLine>, newBottomSizeList: List<SizeLine>?) {
        list = newList
        newBottomSizeList?.let { bottomSizeList = it }
        notifyDataSetChanged()
    }

    fun cleanCheckBoxes() {
        for (size in list) {
            size.isSelected = false
        }
        notifyDataSetChanged()
    }

    fun getChosenSizes(): List<SizeLine> {
        return list
    }

    fun getChosenBottomSizes(): List<SizeLine>{
        return bottomSizeList?.apply { forEachIndexed{index, sizeLine ->
            sizeLine.isSelected = list[index].isSelected
        }
        }.orEmpty()
    }
}