package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemBrandBinding

class BrandsAdapter(private val is0_9Brands: Boolean) :
    RecyclerView.Adapter<BrandsAdapter.BrandViewHolder>() {
    private val brands9 = listOf(
        "İodes (12)",
        "1001 Dress (54)",
        "4F (2)",
        "25Degrees (23)",
        "99Colorspace (0)",
        "«SUBTILLE» (12)",
        "İlke (2)",
        "5+ (65)"
    )
    private val brandsA = listOf(
        "Aksisur (12)",
        "Aliera (54)",
        "Atlantic (2)",
        "Anabel Arto (23)",
        "Among US (0)",
        "ANDETTA (12)"
    )
    var brands9Checked = brands9.map { false }.toMutableList()
    var brandsAChecked = brandsA.map { false }.toMutableList()

    class BrandViewHolder(
        view: View,
        private val brands9Checked: MutableList<Boolean>,
        private val brandsAChecked: MutableList<Boolean>
    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemBrandBinding by viewBinding()
        fun bind(brand: String, position: Int, isBrands9: Boolean) {
            binding.brandTextView.text = brand
            if (isBrands9)
                binding.brandCheckBox.isChecked = brands9Checked[position]
            else
                binding.brandCheckBox.isChecked = brandsAChecked[position]
            binding.brandCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isBrands9)
                    brands9Checked[position] = isChecked
                else
                    brandsAChecked[position] = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        return BrandViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_brand, parent, false),
            brands9Checked,
            brandsAChecked
        )
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        if (is0_9Brands)
            holder.bind(brands9[position], position, is0_9Brands)
        else
            holder.bind(brandsA[position], position, is0_9Brands)
    }

    override fun getItemCount(): Int {
        return if (is0_9Brands)
            brands9.size
        else
            brandsA.size
    }
}