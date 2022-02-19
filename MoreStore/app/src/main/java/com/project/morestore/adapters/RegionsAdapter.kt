package com.project.morestore.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemRegionBinding
import com.project.morestore.models.MaterialLine
import com.project.morestore.models.Region

class RegionsAdapter(private val isForChangeRegion: Boolean, private val isFilter: Boolean, val onClick: (String) -> Unit) : RecyclerView.Adapter<RegionsAdapter.RegionViewHolder>() {
    /*private var regions = listOf(
        Region("Все города", true),
        Region("Москва, Московская область", true),
        Region("Санкт-Петербург, Ленинградская область", true),
        Region("Нижний Новгород, Нижегородская область", true),
        Region("Новосибирск, Новосибирская область", true),
        Region("Екатеринбург, Свердловская область", true),
        Region("Казань, Республика Татарстан", true),
        Region("Челябинск, Челябинская область", true),
        Region("Омск, Омская область", true),
        Region("Самара, Самарская область", true),
        Region("Ростов-на-Дону, Ростовская область", true),
        Region("Уфа, Республика Башкортостан", true),
        Region("Красноярск, Красноярский край", true),
        Region("Воронеж, Воронежская область", true),
        Region("Пермь, Пермский край",true),
        Region("Волгоград, Волгоградская область", true),
        Region("Краснодар, Краснодарский край",true)
    )*/

    private var regions = listOf<Region>()

    class RegionViewHolder(view: View, val onAllMaterial: (isChecked: Boolean) -> Unit, val onChecked: (isChecked: Boolean, position: Int) -> Unit, val isChangeRegion: Boolean,
    onClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val binding: ItemRegionBinding by viewBinding()

        init {
            itemView.setOnClickListener {
                onClick(adapterPosition)
            }
        }

        fun bind(region: Region, isFilter: Boolean) {
            binding.regionTextView.text = region.name
            binding.regionTextView.textSize = if(!isChangeRegion) 14f else 16f
            binding.regionCheckBox.isChecked = region.isChecked ?: false
            binding.regionCheckBox.isVisible = !isChangeRegion
            binding.view36.isVisible = adapterPosition == 0 && isChangeRegion
            if(!isFilter)
                binding.regionCheckBox.isVisible = false
            binding.regionCheckBox.setOnClickListener {
                if(region.name == "Все города"){
                    onAllMaterial(binding.regionCheckBox.isChecked)
                }else {
                    onChecked(binding.regionCheckBox.isChecked, adapterPosition)
                }
            }



        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        return RegionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_region, parent, false),
            { allMaterialChecked ->
                for (region in regions) {
                    region.isChecked = allMaterialChecked
                }
                Log.d("mylog", "onAllMaterial")

                notifyDataSetChanged()

            },{isChecked, position ->
                regions[position].isChecked = isChecked
                if(!isChecked){
                    regions[0].isChecked = false
                    notifyItemChanged(0)
                }else{
                    if(regions.all { it.isChecked == true || it.name == "Все города" }){
                        regions[0].isChecked = true
                        notifyItemChanged(0)
                    }
                }
            }, isForChangeRegion, { position ->
                onClick(regions[position].name)

            })

    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(regions[position], isFilter)
    }

    override fun getItemCount(): Int {
        return regions.size
    }

    fun getCurrentRegions(): List<Region>{
        return regions
    }

    fun updateList(newList: List<Region>) {
        regions = newList
        notifyDataSetChanged()
    }

}