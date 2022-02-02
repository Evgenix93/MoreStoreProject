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
import com.project.morestore.models.ProductBrand

class BrandsAdapter(private val onWishClick: (id: Long) -> Unit) :
    RecyclerView.Adapter<BrandsAdapter.BrandViewHolder>() {
    private var list = listOf<ProductBrand>()
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
    //private var brands9Checked = brands9.map { false }.toMutableList()
    //private var brandsAChecked = brandsA.map { false }.toMutableList()


    class BrandViewHolder(
        view: View,
        //private val brands9Checked: MutableList<Boolean>,
        //private val brandsAChecked: MutableList<Boolean>
        val onCheckBoxClicked: (isChecked: Boolean, position: Int) -> Unit,
        val onWishClick: (position: Int) -> Unit

    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemBrandBinding by viewBinding()
        fun bind(brand: ProductBrand, isNewLetter: Boolean) {
            binding.brandTextView.text = brand.name
            /*if (isBrands9)
                binding.brandCheckBox.isChecked = brands9Checked[position]
            else
                binding.brandCheckBox.isChecked = brandsAChecked[position]
            binding.brandCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isBrands9)
                    brands9Checked[position] = isChecked
                else
                    brandsAChecked[position] = isChecked
            }
        }*/
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
            // brands9Checked,
            //brandsAChecked
            { isChecked, position ->
                list[position].isChecked = isChecked

            }, { position ->
                list[position].isWished ?: run {
                    //list[position].isWished = true
                    onWishClick(list[position].id)
                    Log.d("mylog", list.filter { it.isWished == true }.toString())
                    return@BrandViewHolder
                }
                list[position].isWished?.let {
                    //list[position].isWished = !it
                    onWishClick(list[position].id)
                    return@BrandViewHolder
                }


            })
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        //if (is0_9Brands)
        // holder.bind(brands9[position], position, is0_9Brands)
        //else
        // holder.bind(brandsA[position], position, is0_9Brands)
        val isNewLetter = if (position == 0) {
            true
        } else {
            list[position].name[0] != list[position - 1].name[0] && list[position].name[0].isDigit()
                .not()
        }
        holder.bind(list[position], isNewLetter)
    }

    override fun getItemCount(): Int {
        //return if (is0_9Brands)
        //  brands9.size
        // else
        //  brandsA.size
        return list.size
    }

    /*fun updateBrands9Checked(newList: MutableList<Boolean>){
        brands9Checked = newList
        notifyDataSetChanged()
    }
    fun updateBrandsAChecked(newList: MutableList<Boolean>){
        brandsAChecked = newList
        notifyDataSetChanged()
    }

    fun loadBrands9Checked(): List<Boolean>{
        return brands9Checked
    }

    fun loadBrandsAChecked(): List<Boolean>{
        return brandsAChecked
    }*/

    fun updateList(newList: List<ProductBrand>) {
        list = newList
        notifyDataSetChanged()
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

    fun getWishedBrandsIds(): List<Long> {
        return list.filter { it.isWished == true }.map { it.id }
    }
}