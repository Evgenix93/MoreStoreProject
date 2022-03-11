package com.project.morestore.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemColorBinding
import com.project.morestore.models.Color
import com.project.morestore.models.Property

class ColorsAdapter(private val context: Context, private val isFilter: Boolean, private val  onClick:(Boolean) -> Unit): RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {
   /* private var colors = listOf(
        Color("Все цвета", R.color.black, false),
        Color("черный", R.color.black, false),
        Color("синий", R.color.blue3, false),
        Color("серый", R.color.gray9, false),
        Color("белый", R.color.white, false),
        Color("красный", R.color.red, false),
        Color("зеленый", R.color.green10, false),
        Color("коричневый", R.color.brown2, false),
        Color("бежевый", R.color.brown3, false),
        Color("оранжевый", R.color.orange, false),
        Color("серебряный", R.color.silver, false),
        Color("персиковый", R.color.peach, false),
        Color("жёлтый", R.color.yellow3, false),
        Color("фиолетовый", R.color.purple2, false),
        Color("пурпурный", R.color.purple3, false),
        Color("разноцветный", R.color.white, false)
    )*/

    private var properties = listOf<Property>()
    private var checkedCount = 0

    fun updateColors(newList: List<Property>){
        properties = newList
        checkedCount = properties.filter{it.isChecked == true}.size
        notifyDataSetChanged()
    }

    fun getColors(): List<Property>{
        return properties
    }

    class ColorViewHolder(view: View, private val onAllColors: (Boolean) -> Unit, private val onChecked:(Boolean, Int) -> Unit): RecyclerView.ViewHolder(view){
        private val binding: ItemColorBinding by viewBinding()

        fun bind(property: Property, position: Int, context: Context, isFilter: Boolean, checkedCount: Int){
            binding.colorTextView.text = property.name
            when (property.name) {
                "Все цвета" -> {
                    binding.allColorsCheckBox.isVisible = true
                    binding.colorImageView.isVisible = false
                }
                "разноцветный" -> {
                    binding.allColorsCheckBox.isVisible = false
                    binding.colorImageView.isVisible = true
                    binding.colorImageView.imageTintList = null
                    binding.colorImageView.setImageResource(R.drawable.color2)
                }
                "белый" -> {
                    binding.allColorsCheckBox.isVisible = false
                    binding.colorImageView.isVisible = true
                    binding.colorImageView.imageTintList = null
                    binding.colorImageView.setImageResource(R.drawable.ic_circle)
                }
                else -> {
                    binding.colorImageView.isVisible = true
                    binding.colorImageView.setImageResource(R.drawable.ic_color)
                    binding.allColorsCheckBox.isVisible = false
                    binding.colorImageView.imageTintList = ColorStateList.valueOf(android.graphics.Color.parseColor(property.ico))
                }
            }


            if(property.isChecked == true)
                if(property.name == "Все цвета")
                    binding.allColorsCheckBox.isChecked = true
                else {
                binding.colorImageView.strokeColor = ColorStateList.valueOf(context.resources.getColor(R.color.green))
                binding.colorImageView.strokeWidth = 7f
            }
            else
                if(property.name == "Все цвета")
                    binding.allColorsCheckBox.isChecked = false
            else{
                binding.colorImageView.strokeColor = null
                binding.colorImageView.strokeWidth = 0f
            }

            itemView.setOnClickListener{
                if(isFilter) {
                    property.isChecked = property.isChecked?.not() ?: true
                    if (property.name == "Все цвета") {
                        onAllColors(property.isChecked ?: false)
                    } else {
                        onChecked(property.isChecked ?: false, position)
                    }
                }else{
                    Log.d("Debug", "notFilter click")
                    if(checkedCount < 4){
                        Log.d("Debug", "bind checkedCount = $checkedCount")
                        property.isChecked = property.isChecked?.not() ?: true
                        onChecked(property.isChecked ?: false, position)
                    }else{
                        property.isChecked = false
                        onChecked(property.isChecked ?: false, position)
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false),
            { allColorsChecked ->
                for (property in properties) {
                    property.isChecked = allColorsChecked
                }
                notifyDataSetChanged()
            },{isChecked, position ->
                properties[position].isChecked = isChecked
                onClick(properties.any{it.isChecked == true})
                if(!isFilter)
                    checkedCount = properties.filter{it.isChecked == true}.size
                //Log.d("Debug", "checkedCount = $checkedCount")
                if(!isChecked){
                    if(isFilter) {
                        properties[0].isChecked = false
                        notifyDataSetChanged()
                    }else
                        notifyDataSetChanged()
                }else{
                    if(isFilter)
                    if(properties.all { it.isChecked == true || it.name == "Все цвета" }){
                        properties[0].isChecked = true
                        notifyDataSetChanged()
                    }else{
                       notifyItemChanged(position)
                    }
                    else{

                       notifyDataSetChanged()
                    }
                }
            })
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        Log.d("Debug", "onBindViewHolder checkedCount = $checkedCount")
        holder.bind(properties[position], position,  context, isFilter, checkedCount)
    }

    override fun getItemCount(): Int {
        return properties.size
    }
}