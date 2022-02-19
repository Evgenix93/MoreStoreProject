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
import com.project.morestore.databinding.ItemMaterialLineBinding
import com.project.morestore.models.MaterialLine

class MaterialAdapter(private val context: Context, private val isFilter: Boolean) : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    private var list = listOf<MaterialLine>()

    class MaterialViewHolder(view: View, val onAllMaterial: (isChecked: Boolean) -> Unit, val onChecked: (isChecked: Boolean, position: Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val binding: ItemMaterialLineBinding by viewBinding()

        fun bind(context: Context, material: MaterialLine, isFilter: Boolean) {
            binding.materialNameTextView.text = material.name
            binding.ExcellentCheckBox.isChecked = material.isSelected
            if(isFilter){
                binding.checkImageView.isVisible = false
            }else
                binding.ExcellentCheckBox.isVisible = false

            binding.ExcellentCheckBox.setOnClickListener {
                if(material.name == "Все материалы"){
                    onAllMaterial(binding.ExcellentCheckBox.isChecked)
                }else {
                    onChecked(binding.ExcellentCheckBox.isChecked, adapterPosition)
                }
            }
            if(!isFilter)
            itemView.setOnClickListener {
                if(material.isSelected) {
                    binding.checkImageView.imageTintList = null
                    material.isSelected = false
                }
                else {
                    binding.checkImageView.imageTintList =
                        ColorStateList.valueOf(context.resources.getColor(R.color.green))
                    material.isSelected = true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        return MaterialViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_material_line, parent, false),
         { allMaterialChecked ->
            for (material in list) {
                material.isSelected = allMaterialChecked
            }
            Log.d("mylog", "onAllMaterial")
             notifyDataSetChanged()

        },{isChecked, position ->
                list[position].isSelected = isChecked
                if(!isChecked){
                    list[0].isSelected = false
                    notifyItemChanged(0)
                }else{
                    if(list.all { it.isSelected || it.name == "Все материалы" }){
                        list[0].isSelected = true
                        notifyItemChanged(0)
                    }
                }
        })

    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(context, list[position], isFilter)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getCurrentMaterials(): List<MaterialLine>{
        return list
    }

    fun updateList(newList: List<MaterialLine>) {
        list = newList
        notifyDataSetChanged()
    }
}