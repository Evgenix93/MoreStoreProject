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
import com.project.morestore.models.CreateProductData
import com.project.morestore.models.Option
import java.io.File

class OptionsAdapter(private val context: Context, val onClick: (Int) -> Unit): RecyclerView.Adapter<OptionsAdapter.OptionViewHolder>() {
    private val options = listOf(
        Option("Фотографии", false),
        Option("Состояние", false),
        Option("Стоимость", false),
        Option("Размер", false),
        Option("Описание", false),
        Option("Цвет", false),
        Option("Материал", false),
        Option("Местоположение", true)
    )

    class OptionViewHolder(view: View, onClick: (Int) -> Unit): RecyclerView.ViewHolder(view){
        private val binding: ItemOptionBinding by viewBinding()

        init {
            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }

        fun bind(option: Option, context: Context){
            binding.nameTextView.text = option.name
            if(adapterPosition > 4 )
                binding.starTextView.isVisible = false
            if(option.isChecked)
                binding.checkImageView.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.green))
            else
                binding.checkImageView.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.gray1))


                binding.regionTextView.isVisible = option.name == "Местоположение"
                if(option.name == "Местоположение")
                    binding.regionTextView.text = option.address

        }
    }

    fun updateList(createProductData: CreateProductData){
        val conditions = createProductData.property?.filter{it.propertyCategory == 11L}.orEmpty()
        val price = createProductData.price
        val size = createProductData.property?.firstOrNull{
            it.propertyCategory < 10
        }
        val about = createProductData.about
        val colors = createProductData.property?.filter{it.propertyCategory == 12L}.orEmpty()
        val materials = createProductData.property?.filter{it.propertyCategory == 13L}.orEmpty()
        val region = createProductData.address
        options[1].isChecked = conditions.isNotEmpty()
        options[2].isChecked = price != null && price != "0.0"
        options[3].isChecked = size != null
        options[4].isChecked = about != null && about != ""
        options[5].isChecked = colors.isNotEmpty()
        options[6].isChecked = materials.isNotEmpty()
        options[7].isChecked = region != null
        options[7].address = region
        notifyDataSetChanged()
    }

    fun updatePhotoInfo(photos: MutableMap<Int, File>){
        options[0].isChecked = photos.isNotEmpty()
        notifyItemChanged(0)
    }

    fun getList(): List<Option>{
        return options
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
      return OptionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)){ position ->
          onClick(position)

      }
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position], context)
    }

    override fun getItemCount(): Int {
        return options.size
    }
}