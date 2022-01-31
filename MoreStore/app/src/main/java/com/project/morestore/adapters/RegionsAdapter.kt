package com.project.morestore.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemRegionBinding
import com.project.morestore.models.MaterialLine
import com.project.morestore.models.Region

class RegionsAdapter : RecyclerView.Adapter<RegionsAdapter.RegionViewHolder>() {
    private var regions = listOf(
        Region("Все города", false),
        Region("Москва, Московская область", false),
        Region("Санкт-Петербург, Ленинградская область", false),
        Region("Нижний Новгород, Нижегородская область", false),
        Region("Новосибирск, Новосибирская область", false),
        Region("Екатеринбург, Свердловская область", false),
        Region("Казань, Республика Татарстан", false),
        Region("Челябинск, Челябинская область", false),
        Region("Омск, Омская область", false),
        Region("Самара, Самарская область", false),
        Region("Ростов-на-Дону, Ростовская область", false),
        Region("Уфа, Республика Башкортостан", false),
        Region("Красноярск, Красноярский край", false),
        Region("Воронеж, Воронежская область", false),
        Region("Пермь, Пермский край",false),
        Region("Волгоград, Волгоградская область", false),
        Region("Краснодар, Краснодарский край",false)
    )

    class RegionViewHolder(view: View, val onAllMaterial: (isChecked: Boolean) -> Unit, val onChecked: (isChecked: Boolean, position: Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val binding: ItemRegionBinding by viewBinding()

        fun bind(region: Region) {
            binding.regionTextView.text = region.name
            binding.regionCheckBox.isChecked = region.isChecked
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
                    if(regions.all { it.isChecked || it.name == "Все города" }){
                        regions[0].isChecked = true
                        notifyItemChanged(0)
                    }
                }
            })

    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(regions[position])
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