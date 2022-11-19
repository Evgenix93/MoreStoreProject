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

class MaterialAdapter(private val context: Context, private val isFilter: Boolean, private val onClick: (Boolean) -> Unit) : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    private var list = emptyList<MaterialLine>()
    private var checkedCount = 0

    class MaterialViewHolder(view: View, val onAllMaterial: (isChecked: Boolean) -> Unit, val onChecked: (isChecked: Boolean, position: Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val binding: ItemMaterialLineBinding by viewBinding()

        fun bind(context: Context, material: MaterialLine, isFilter: Boolean, checkedCount: Int) {
            binding.materialNameTextView.text = material.name
            binding.excellentCheckBox.isChecked = material.isSelected
            if(isFilter){
                binding.checkImageView.isVisible = false
            }else
                binding.excellentCheckBox.isVisible = false

            binding.excellentCheckBox.setOnClickListener {
                if(material.name == ALL_MATERIALS){
                    onAllMaterial(binding.excellentCheckBox.isChecked)
                }else {
                    onChecked(binding.excellentCheckBox.isChecked, adapterPosition)
                }
            }

            if(material.isSelected) {
                binding.checkImageView.imageTintList = ColorStateList.valueOf(context.resources.getColor(R.color.green))
            }
            else {
                binding.checkImageView.imageTintList = null
            }

            if(!isFilter)
            itemView.setOnClickListener {
                if(checkedCount < 4)
                    if(material.isSelected) {
                        material.isSelected = false
                        onChecked(false, adapterPosition)
                    }
                    else {
                        material.isSelected = true
                        onChecked(true, adapterPosition)
                }
                else{
                    material.isSelected = false
                    onChecked(false, adapterPosition)
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
                onClick(list.any{it.isSelected})
                checkedCount = list.filter{ it.isSelected }.size
                if(!isChecked){
                    if(isFilter) {
                        list[0].isSelected = false
                        notifyDataSetChanged()
                    }
                    else
                        notifyDataSetChanged()
                }else{
                    if(isFilter) {
                        if (list.all { it.isSelected || it.name == ALL_MATERIALS }) {
                            list[0].isSelected = true
                            notifyDataSetChanged()
                        }
                    }else
                        notifyDataSetChanged()
                }
        })

    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(context, list[position], isFilter, checkedCount)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getCurrentMaterials(): List<MaterialLine>{
        return list
    }

    fun updateList(newList: List<MaterialLine>) {
        list = newList
        checkedCount = list.filter{it.isSelected}.size
        notifyDataSetChanged()
    }

    companion object {
        const val ALL_MATERIALS =  "Все материалы"
    }
}