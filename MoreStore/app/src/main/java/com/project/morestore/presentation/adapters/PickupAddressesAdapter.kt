package com.project.morestore.presentation.adapters

import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.data.models.CdekAddress
import com.project.morestore.presentation.fragments.MapMarkerPickupsFragment
import com.project.morestore.util.inflater

class PickupAddressesAdapter(val callback :(CdekAddress) -> Unit) :RecyclerView.Adapter<PickupAddressesAdapter.PickupAddressHolder>() {
    private var items = listOf<CdekAddress>()
    private var viewSelected :RadioButton? = null
    private var selected :CdekAddress? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupAddressHolder {
        val radioButton :RadioButton = parent.inflater.inflate(R.layout.item_pickupaddress, parent, false) as RadioButton
        return PickupAddressHolder(radioButton).also{
            radioButton.setOnClickListener { view ->
                if(viewSelected == view) return@setOnClickListener
                viewSelected?.isChecked = false
                viewSelected = radioButton
                radioButton.isChecked = true

                callback(items[it.adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: PickupAddressHolder, position: Int) {
        val item = items[position]
        val typeText = if(item.type == CdekAddress.TYPE_PVZ) holder.itemView.context.getString(R.string.pvz)
                       else holder.itemView.context.getString(R.string.postamat)
        holder.button.text = "${item.location.address} ($typeText)"
        holder.text = item.location.address
        holder.button.isChecked = item == selected
    }

    override fun getItemCount() = items.size

    fun setItems(newItems :List<CdekAddress>){
        items = newItems
        notifyDataSetChanged()
    }

    fun select(address :CdekAddress){
        selected = address
        notifyDataSetChanged()
    }

    inner class PickupAddressHolder(
        val button :RadioButton
    ) :RecyclerView.ViewHolder(button){
        lateinit var text :String
    }
}