package com.project.morestore.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCategoryBinding
import com.project.morestore.data.models.Category

class CategoryAdapter(
    private val isOnboarding: Boolean,
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
        private val segments1Checked: MutableList<Boolean>,
        private val checkBoxClick: (Int, Boolean) -> Unit,
        private val checkBoxClick2: (Int, Boolean) -> Unit,
        private val allCheckCallback: (Boolean) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding: ItemCategoryBinding by viewBinding()

        fun bind(category: Category, isAllChecked: Boolean, position: Int, isOnboarding: Boolean, isChecked: Boolean) {
            binding.titleTextView.text = category.name
            when (category.id) {
                1 -> binding.descriptionTextView.text =
                    itemView.context.getString(R.string.louis_vuitton_gucci_prada_dolce_gabbana)
                2 -> binding.descriptionTextView.text =
                    itemView.context.getString(R.string.tommy_hilfiger_michael_kors_furla_calvin_klein_n)
                3 -> binding.descriptionTextView.text =
                    itemView.context.getString(R.string.zara_h_m_bershka_asos_mango_n)
                4 -> binding.descriptionTextView.text = itemView.context.getString(R.string.economy_subtitle)
            }



                binding.categoryCheckBox.isChecked = isChecked

            binding.categoryCheckBox.setOnClickListener { _ ->
                if(!isOnboarding)

                   checkBoxClick2(adapterPosition, binding.categoryCheckBox.isChecked)
                else
                    segments1Checked[position] = binding.categoryCheckBox.isChecked
                if (category.id == 0) {
                    Log.d("Debug", "allCheck")
                    allCheckCallback(binding.categoryCheckBox.isChecked)
                } else {
                    checkBoxClick(category.id, binding.categoryCheckBox.isChecked)
                    checkBoxClick2(adapterPosition, binding.categoryCheckBox.isChecked)
                }
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
        val allChecked = newList.all { it }
        segments1Checked = (mutableListOf(allChecked) + newList).toMutableList()
        Log.d("mylog", "segments $segments2Checked")
        notifyDataSetChanged()
    }

    fun loadSegments2Checked(): MutableList<Boolean>{
        return segments2Checked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false),
            segments1Checked,
            checkBoxClick,
            {position, isChecked ->
                if(isOnboarding.not())
              segments2Checked[position] = isChecked
                else
                    segments2Checked[position - 1] = isChecked
            }
        ) { allChecked ->
            segments2Checked = mutableListOf(allChecked, allChecked, allChecked, allChecked)
            segments1Checked =
                mutableListOf(allChecked, allChecked, allChecked, allChecked, allChecked)
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (isOnboarding)
            holder.bind(segments1[position], isAllChecked, position, isOnboarding, segments1Checked[position])//false)
        else
            holder.bind(segments2[position], isAllChecked, position, isOnboarding, segments2Checked[position])
    }

    override fun getItemCount(): Int {
        return if (isOnboarding)
            segments1.size
        else
            segments2.size
    }

    fun clearCheckboxes(){
        val list = mutableListOf<Boolean>()
        for (segment in segments2Checked){
            list.add(false)
        }
        updateSegmentsChecked(list)

    }
}