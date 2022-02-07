package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemProductCategoryBinding
import com.project.morestore.models.ProductCategory

class ProductCategoriesAdapter: RecyclerView.Adapter<ProductCategoriesAdapter.ProductCategoryViewHolder>() {
    private var productCategories = emptyList<ProductCategory>()

    class ProductCategoryViewHolder(view: View, val onAllCategories: (isChecked: Boolean) -> Unit, val onChecked: (isChecked: Boolean, position: Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val binding: ItemProductCategoryBinding by viewBinding()

        fun bind(productCategory: ProductCategory) {
            binding.nameTextView.text = productCategory.name
            binding.categoryCheckBox.isChecked = productCategory.isChecked ?: false
            binding.categoryCheckBox.setOnClickListener {
                if(productCategory.name == "Все категории"){
                    onAllCategories(binding.categoryCheckBox.isChecked)
                }else {
                    onChecked(binding.categoryCheckBox.isChecked, adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        return ProductCategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_category, parent, false),
            { allCategoriesChecked ->
                for (productCategory in productCategories) {
                    productCategory.isChecked = allCategoriesChecked
                }
                notifyDataSetChanged()
            },{isChecked, position ->
                productCategories[position].isChecked = isChecked
                if(!isChecked){
                    productCategories[0].isChecked = false
                    notifyItemChanged(0)
                }else{
                    if(productCategories.all { it.isChecked == true || it.name == "Все категории" }){
                        productCategories[0].isChecked = true
                        notifyItemChanged(0)
                    }
                }
            })

    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        holder.bind(productCategories[position])
    }

    override fun getItemCount(): Int {
        return productCategories.size
    }

    fun getProductCategories(): List<ProductCategory>{
        return productCategories
    }

    fun updateList(newList: List<ProductCategory>) {
        productCategories =   newList
        notifyDataSetChanged()
    }
}