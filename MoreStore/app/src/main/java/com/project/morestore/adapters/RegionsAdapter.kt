package com.project.morestore.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemBrandBinding
import com.project.morestore.databinding.ItemRegionBinding

class RegionsAdapter : RecyclerView.Adapter<RegionsAdapter.RegionViewHolder>() {
    private val regions = listOf(
        "Все города",
        "Москва, Московская область",
        "Санкт-Петербург, Ленинградская область",
        "Нижний Новгород, Нижегородская область",
        "Новосибирск, Новосибирская область",
        "Екатеринбург, Свердловская область",
        "Казань, Республика Татарстан",
        "Челябинск, Челябинская область",
        "Омск, Омская область",
        "Самара, Самарская область",
        "Ростов-на-Дону, Ростовская область",
        "Уфа, Республика Башкортостан",
        "Красноярск, Красноярский край",
        "Воронеж, Воронежская область",
        "Пермь, Пермский край",
        "Волгоград, Волгоградская область",
        "Краснодар, Краснодарский край"
    )


    var regionsChecked = regions.map { true }.toMutableList()


    class RegionViewHolder(
        view: View,
        private val regionsChecked: MutableList<Boolean>,
        private val isAllChecked: (Boolean) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemRegionBinding by viewBinding()
        fun bind(brand: String,  position: Int) {
            binding.regionTextView.text = brand
            binding.regionCheckBox.isChecked = regionsChecked[position]
            binding.regionCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (position == 0) {
                    if (isChecked) {
                        Log.d("Debug", "isAllChecked = true")
                        regionsChecked[position] = isChecked
                        isAllChecked(isChecked)
                    } else if (regionsChecked.all { it }) {
                        regionsChecked[position] = isChecked
                        isAllChecked(isChecked)
                    }
                } else {
                     regionsChecked[position] = isChecked
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        return RegionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_region, parent, false),
            regionsChecked
        ){
            regionsChecked.forEachIndexed {index,_->
                regionsChecked[index] = it
            }
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(regions[position], position)
    }

    override fun getItemCount(): Int {
        return regions.size
    }
}