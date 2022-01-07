package com.project.morestore.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCategoryBinding
import com.project.morestore.models.Category

class CategoryAdapter(private val context: Context, private val checkBoxClick: (Int, Boolean) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.Holder>() {
    private var categories = emptyList<Category>()
    private var isAllChecked = false

    class Holder(
        view: View,
        private val context: Context,
        private val checkBoxClick: (Int, Boolean) -> Unit,
        private val allCheckCallback: (Boolean) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemCategoryBinding by viewBinding()

        fun bind(category: Category, isAllChecked: Boolean) {
            binding.titleTextView.text = category.name
            when (category.id) {
                1 -> binding.descriptionTextView.text =
                    context.getString(R.string.louis_vuitton_gucci_prada_dolce_gabbana)
                2 -> binding.descriptionTextView.text =
                    context.getString(R.string.tommy_hilfiger_michael_kors_furla_calvin_klein_n)
                3 -> binding.descriptionTextView.text =
                    context.getString(R.string.zara_h_m_bershka_asos_mango_n)
                4 -> binding.descriptionTextView.text = context.getString(R.string.economy_subtitle)
            }

            binding.categoryCheckBox.isChecked = isAllChecked
            binding.categoryCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (category.id == -1) {
                    Log.d("Debug", "allCheck")
                    allCheckCallback(isChecked)
                }else
                    checkBoxClick(category.id, isChecked)
            }
        }
    }

    fun updateList(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false),
            context,
            checkBoxClick
        ){
           isAllChecked = it
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (position == 0)
            holder.bind(Category(-1, "Выбрать все"), isAllChecked)
        else
            holder.bind(categories[position - 1], isAllChecked)
    }

    override fun getItemCount(): Int {
        return categories.size + 1
    }
}