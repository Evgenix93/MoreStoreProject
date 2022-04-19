package com.project.morestore.adapters

import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.util.inflater

class PickupAddressesAdapter(val callback :(String) -> Unit) :RecyclerView.Adapter<PickupAddressesAdapter.PickupAddressHolder>() {
    private var items = listOf<String>()
    private var selected :RadioButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickupAddressHolder {
        val radioButton :RadioButton = parent.inflater.inflate(R.layout.item_pickupaddress, parent, false) as RadioButton
        return PickupAddressHolder(radioButton).also{
            radioButton.setOnClickListener { view ->
                if(view == selected) return@setOnClickListener
                selected?.isChecked = false
                callback.invoke(items[it.adapterPosition])
                (view as RadioButton).also {
                    selected = it
                    it.isChecked = true
                }
            }
        }
    }

    override fun onBindViewHolder(holder: PickupAddressHolder, position: Int) {
        holder.button.text = items[position]
        holder.text = items[position]
    }

    override fun getItemCount() = items.size

    fun setItems(newItems :List<String>){
        items = newItems
        notifyDataSetChanged()
    }

    inner class PickupAddressHolder(
        val button :RadioButton
    ) :RecyclerView.ViewHolder(button){
        lateinit var text :String
    }
}