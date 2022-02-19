package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemBrandCreateProductBinding
import com.project.morestore.databinding.ItemCreateProductElementBinding
import com.project.morestore.models.ProductBrand
import com.project.morestore.models.ProductCategory

class BrandCreateProductAdapter: RecyclerView.Adapter<BrandCreateProductAdapter.BrandViewHolder>() {
    var list = listOf<ProductBrand>()

    class BrandViewHolder(view: View): RecyclerView.ViewHolder(view){
    private val binding: ItemBrandCreateProductBinding by viewBinding()

    init {
        itemView.setOnClickListener {
            //onClick(adapterPosition)
        }
    }

    fun bind(brand: ProductBrand, isNewLetter: Boolean){
        binding.brandTextView.text = brand.name
        binding.firstLetterTextView.text = brand.name[0].toString()
        binding.firstLetterTextView.isVisible = isNewLetter

    }

}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
    return BrandViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_brand_create_product, parent, false)
    )

}

override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
    val isNewLetter = if (position == 0) {
        true
    } else {
        list[position].name[0] != list[position - 1].name[0] && list[position].name[0].isDigit()
            .not()
    }
    holder.bind(list[position], isNewLetter)

}

override fun getItemCount(): Int {
    return list.size

}

fun updateList(newList: List<ProductBrand>){
    list = newList
    notifyDataSetChanged()
}
}