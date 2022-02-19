package com.project.morestore.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemOptionBinding
import com.project.morestore.models.Option

class OptionsAdapter(private val context: Context): RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {
    private val options = listOf(
        Option("Фотографии *", false),
        Option("Состояние *", false),
        Option("Стоимость *", false),
        Option("Размер *", false),
        Option("Описание *", false),
        Option("Цвет", false),
        Option("Материал", false),
        Option("Местоположение", true)
    )

    class OptionViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding: ItemOptionBinding by viewBinding()

        fun bind(option: Option, context: Context){
            binding.nameTextView.text = option.name
            if(adapterPosition > 4 )
                binding.starTextView.isVisible = false
            if(option.isChecked)
                binding.checkImageView.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.green))
            else
                binding.checkImageView.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.gray1))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
      return OptionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false))
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position], context)
    }

    override fun getItemCount(): Int {
        return options.size
    }
}