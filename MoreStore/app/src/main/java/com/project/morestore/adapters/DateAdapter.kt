package com.project.morestore.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemDateBinding

class DateAdapter(val isDay: Boolean): RecyclerView.Adapter<DateAdapter.DateViewHolder>() {
    private var selectedPosition = 0


    class DateViewHolder(view: View, val isDay: Boolean): RecyclerView.ViewHolder(view){
        private val binding: ItemDateBinding by viewBinding()
        private val months = MONTHS


        fun bind(selectedPosition: Int){
            binding.dayTextView.text = if(isDay) (adapterPosition + 1).toString() else months[adapterPosition]
            binding.dayTextView.typeface = Typeface.DEFAULT
            if(selectedPosition == adapterPosition){
                binding.dayTextView.typeface = Typeface.DEFAULT_BOLD

            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false), isDay)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(selectedPosition)

    }

    override fun getItemCount(): Int {
        return 31
    }

    fun updateSelectedDate(position: Int){
        selectedPosition = position
        notifyDataSetChanged()


    }

    companion object {
         val MONTHS = listOf(
            "Январь",
            "Февраль",
            "Март",
            "Апрель",
            "Май",
            "Июнь",
            "Июль",
            "Август",
            "Сентябрь",
            "Октябрь",
            "Ноябрь",
            "Декабрь"
        )
    }
}