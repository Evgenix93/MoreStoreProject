package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemMenuSliderBinding
import com.project.morestore.data.models.slidermenu.SliderMenu

class SliderMenuAdapter<T>(
        private val items: List<SliderMenu<T>>,
        private val onItemClickListener: (T) -> Unit
) : RecyclerView.Adapter<SliderMenuAdapter.SliderViewHolder>() {


    private lateinit var currentSelected: SliderViewHolder


    class SliderViewHolder(menuItem: View) : RecyclerView.ViewHolder(menuItem) {
        val binding: ItemMenuSliderBinding by viewBinding()

        fun <T> bind(sliderMenu: SliderMenu<T>) {
            with(binding) {
                if (sliderMenu.icon == null) {
                    sliderIcon.visibility = View.GONE
                } else {
                    sliderIcon.setImageResource(sliderMenu.icon)
                }
                sliderContent.text = sliderMenu.content
                sliderCount.text = sliderMenu.itemsCount.toString()

                if (!sliderMenu.isSelected) {
                    val blackColor = ContextCompat.getColor(itemView.context, R.color.black)
                    if (sliderMenu.icon != null) {
                        sliderIcon.setColorFilter(blackColor)
                    }
                    sliderContent.setTextColor(blackColor)
                    sliderCount.setTextColor(blackColor)
                    sliderContainer.background = null
                } else {
                    sliderContainer.setBackgroundResource(R.drawable.product_status_btn_shape)
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Recycler impl
    ///////////////////////////////////////////////////////////////////////////



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val itemView =
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_menu_slider, parent, false)
        return SliderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {



        holder.bind(items[position])
        holder.binding.sliderContainer.setOnClickListener() {

            if (items[position].isSelected) {
                onItemClickListener(items[position].type)
                return@setOnClickListener
            }

            items[position].isSelected = true
            notifyItemChanged(position)

            currentSelected = holder

            onItemClickListener(items[position].type)
        }
    }


    override fun getItemCount() = items.size

    fun changeCartItemsSize(size: Int){
        changeItemsCount(0, size)
    }
    fun changeOrdersItemsSize(size: Int){
        changeItemsCount(1, size)
    }
    fun changeOrderHistorySize(size: Int){
        changeItemsCount(3, size)
    }

    fun changeSalesItemsSize(size: Int){
        changeItemsCount(2, size)
    }

    fun changeSalesHistorySize(size: Int){
        changeItemsCount(4, size)
    }

    private fun changeItemsCount(position: Int, size: Int){
        items[position].itemsCount = size.toUInt()
        notifyItemChanged(position)
    }
}