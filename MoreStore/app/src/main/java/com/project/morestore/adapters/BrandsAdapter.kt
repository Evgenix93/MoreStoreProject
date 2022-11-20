package com.project.morestore.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemBrandBinding
import com.project.morestore.data.models.ProductBrand

class BrandsAdapter(private val onWishClick: (id: Long) -> Unit, private val onClick: () -> Unit) :
    RecyclerView.Adapter<BrandsAdapter.BrandViewHolder>() {
    private var list = listOf<ProductBrand>().toMutableList()

    class BrandViewHolder(
        view: View,
        val onCheckBoxClicked: (isChecked: Boolean, position: Int) -> Unit,
        val onWishClick: (position: Int) -> Unit

    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemBrandBinding by viewBinding()
        fun bind(brand: ProductBrand, isNewLetter: Boolean) {
            binding.brandTextView.text = brand.name

            binding.isWishedImageView.setImageResource(if (brand.isWished == true) R.drawable.ic_wished else R.drawable.ic_like3)

            binding.brandCheckBox.isChecked = brand.isChecked ?: false
            binding.firstLetterTextView.isVisible = isNewLetter
            binding.firstLetterTextView.text =
                if (brand.name[0].isDigit()) "0-9" else brand.name[0].toString()
            binding.brandCheckBox.setOnClickListener {
                onCheckBoxClicked(binding.brandCheckBox.isChecked, adapterPosition)
            }
            binding.isWishedImageView.setOnClickListener {
                onWishClick(adapterPosition)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        return BrandViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_brand, parent, false),

            { isChecked, position ->
                list[position].isChecked = isChecked
                onClick()

            }, { position ->
                list[position].isWished ?: run {
                    onWishClick(list[position].id)
                    Log.d("mylog", list.filter { it.isWished == true }.toString())
                    return@BrandViewHolder
                }
                list[position].isWished?.let {
                    onWishClick(list[position].id)
                    return@BrandViewHolder
                }


            })
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



    fun updateList(newList: List<ProductBrand>) {
        list = newList.toMutableList()
        notifyDataSetChanged()
    }

    fun updateList2(newList: List<ProductBrand>){
        list.forEachIndexed{index, productBrand ->
            newList.forEach{
                if(productBrand.id == it.id)
                    list[index] = it
                notifyItemChanged(index)
            }
        }
    }

    fun clearCheckboxes(){
        for (brand in list){
            brand.isChecked = false
            notifyDataSetChanged()
        }
    }

    fun updateWishedInfo(ids: List<Long>, isAllWished: Boolean) {
        for (id in ids) {
            val brand = list.find { it.id == id }?.apply {
                isWished = if (isAllWished) true else {
                    if (isWished == null) true else !isWished!!
                }
            }
            notifyItemChanged(list.indexOf(brand))
        }

    }

    fun getCurrentList(): List<ProductBrand> {
        return list
    }

}