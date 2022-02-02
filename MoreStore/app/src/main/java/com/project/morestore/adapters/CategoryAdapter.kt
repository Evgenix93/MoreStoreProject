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

class CategoryAdapter(
    private val isOnboarding: Boolean,
    private val context: Context,
    private val checkBoxClick: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.Holder>() {
    private var segments1 = emptyList<Category>()
    private var segments2 = listOf(
        Category(1, "Люкс"),
        Category(2, "Мидл-сегмент"),
        Category(3, "Масс-маркет"),
        Category(4, "Эконом")
    )
    private var isAllChecked = false
    private var segments1Checked = mutableListOf<Boolean>()
    private var segments2Checked = segments2.map { false }.toMutableList()


    class Holder(
        view: View,
        private val context: Context,
        private val segments1Checked: MutableList<Boolean>,
        private val segments2Checked: MutableList<Boolean>,
        private val checkBoxClick: (Int, Boolean) -> Unit,
        private val checkBoxClick2: (Int, Boolean) -> Unit,
        private val allCheckCallback: (Boolean) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemCategoryBinding by viewBinding()

        fun bind(category: Category, isAllChecked: Boolean, position: Int, isOnboarding: Boolean, isChecked: Boolean) {
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


            if (isOnboarding)
                binding.categoryCheckBox.isChecked = isAllChecked
              else
                binding.categoryCheckBox.isChecked = isChecked
            
            binding.categoryCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if(!isOnboarding)
               // segments2Checked[position] = isChecked
                   checkBoxClick2(adapterPosition, isChecked)
                else
                    segments1Checked[position] = isChecked
                if (category.id == 0) {
                    Log.d("Debug", "allCheck")
                    allCheckCallback(isChecked)
                } else
                    checkBoxClick(category.id, isChecked)
            }
        }
    }

    fun updateList(newList: List<Category>) {
        segments1 = listOf(Category(0, "Выбрать все")) + newList
        segments1Checked = segments1.map{false}.toMutableList()
        notifyDataSetChanged()
    }

    fun updateSegmentsChecked(newList: MutableList<Boolean>){
        segments2Checked = newList
        Log.d("mylog", "segments $segments2Checked")
        notifyDataSetChanged()
    }

    fun loadSegments1Checked(): List<Boolean>{
        return segments1Checked.apply { removeFirst() }
    }

    fun loadSegments2Checked(): MutableList<Boolean>{
        return segments2Checked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false),
            context,
            segments1Checked,
            segments2Checked,
            checkBoxClick,
            {position, isChecked ->
              segments2Checked[position] = isChecked
            },
         {
            isAllChecked = it
            notifyDataSetChanged()
        })
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (isOnboarding)
            holder.bind(segments1[position], isAllChecked, position, isOnboarding, false)
        else
            holder.bind(segments2[position], isAllChecked, position, isOnboarding, segments2Checked[position])
    }

    override fun getItemCount(): Int {
        return if (isOnboarding)
            segments1.size
        else
            segments2.size
    }
}