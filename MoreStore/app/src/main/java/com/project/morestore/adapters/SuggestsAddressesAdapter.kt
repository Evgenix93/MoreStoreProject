package com.project.morestore.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.databinding.ItemSuggestAddressBinding
import com.project.morestore.models.SuggestAddress
import com.project.morestore.util.inflater

class SuggestsAddressesAdapter :RecyclerView.Adapter<SuggestsAddressesAdapter.SuggestsAddressHolder>() {
    private var items = listOf<SuggestAddress>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestsAddressHolder {
        return SuggestsAddressHolder(ItemSuggestAddressBinding.inflate(parent.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: SuggestsAddressHolder, position: Int) {
        val item = items[position]
        with(holder.views){
            address.text = item.address
            distance.text = item.distance
            region.text = item.region
        }
    }

    override fun getItemCount() = items.size

    fun setItems(newItems :List<SuggestAddress>){
        items = newItems
        notifyDataSetChanged()
    }

    inner class SuggestsAddressHolder(
        val views :ItemSuggestAddressBinding
    ) :RecyclerView.ViewHolder(views.root)
}
