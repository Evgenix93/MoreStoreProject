package com.project.morestore.adapters

import android.graphics.Color.LTGRAY
import android.graphics.Color.TRANSPARENT
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.databinding.ItemSuggestAddressBinding
import com.project.morestore.models.SuggestAddress
import com.project.morestore.util.inflater

class SuggestsAddressesAdapter(private val onSelectChange: (Boolean) -> Unit) :RecyclerView.Adapter<SuggestsAddressesAdapter.SuggestsAddressHolder>() {
    var selected :SuggestAddress? = null
    private var items = arrayOf<SuggestAddress>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestsAddressHolder {
        return SuggestsAddressHolder(ItemSuggestAddressBinding.inflate(parent.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SuggestsAddressHolder, position: Int) {
        val item = items[position]
        with(holder.views){
            address.text = item.address
            distance.text = item.distance
            region.text = item.region
            root.setBackgroundColor(if(item === selected) LTGRAY else TRANSPARENT)
        }
    }

    override fun getItemCount() = items.size

    fun setItems(newItems :Array<SuggestAddress>){
        items = newItems
        selected = null
        onSelectChange(false)
        notifyDataSetChanged()
    }

    inner class SuggestsAddressHolder(
        val views :ItemSuggestAddressBinding
    ) :RecyclerView.ViewHolder(views.root){
        init {
            views.root.setOnClickListener{
                val item = items[adapterPosition]
                if(item !== selected){
                    selected = item
                    onSelectChange(true)
                    notifyDataSetChanged()
                }
            }
        }
    }
}
