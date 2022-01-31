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
import com.project.morestore.databinding.ItemColorBinding
import com.project.morestore.models.Color

class ColorsAdapter(private val context: Context): RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {
    private var colors = listOf(
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
    )

    fun updateColors(newList: List<Color>){
        colors = newList
        notifyDataSetChanged()
    }

    fun getColors(): List<Color>{
        return colors
    }

    class ColorViewHolder(view: View, private val onAllColors: (Boolean) -> Unit, private val onChecked:(Boolean, Int) -> Unit): RecyclerView.ViewHolder(view){
        private val binding: ItemColorBinding by viewBinding()

        fun bind(color: Color, position:Int,  context: Context){
            binding.colorTextView.text = color.name
            if(color.name == "Все цвета") {
                binding.allColorsCheckBox.isVisible = true
                binding.colorImageView.isVisible = false
            } else
            if(color.name == "разноцветный") {
                binding.allColorsCheckBox.isVisible = false
                binding.colorImageView.setImageResource(R.drawable.color2)
            }
            else if(color.name == "белый")
                binding.colorImageView.setImageResource(R.drawable.ic_circle)
            else
               binding.colorImageView.imageTintList = ColorStateList.valueOf(context.resources.getColor(color.color))
            if(color.isChecked){
                binding.colorImageView.strokeColor = ColorStateList.valueOf(context.resources.getColor(R.color.green))
                binding.colorImageView.strokeWidth = 5f
            }
            else{
                binding.colorImageView.strokeColor = null
                binding.colorImageView.strokeWidth = 0f
            }
            itemView.setOnClickListener{
                color.isChecked = !color.isChecked
                binding.allColorsCheckBox.isChecked = color.isChecked
                if(color.name == "Все цвета"){
                    onAllColors(color.isChecked)
                }
                else{
                    onChecked(color.isChecked, adapterPosition)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false),
            { allColorsChecked ->
                for (color in colors) {
                    color.isChecked = allColorsChecked
                }
                notifyDataSetChanged()
            },{isChecked, position ->
                colors[position].isChecked = isChecked
                if(!isChecked){
                    colors[0].isChecked = false
                    notifyItemChanged(0)
                }else{
                    if(colors.all { it.isChecked || it.name == "Все цвета" }){
                        colors[0].isChecked = true
                        notifyItemChanged(0)
                    }
                }
            })
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colors[position], position,  context)
    }

    override fun getItemCount(): Int {
        return colors.size
    }
}