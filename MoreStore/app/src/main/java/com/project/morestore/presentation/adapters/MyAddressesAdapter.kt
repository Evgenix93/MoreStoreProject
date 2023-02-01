package com.project.morestore.presentation.adapters

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.databinding.ItemMyaddressBinding
import com.project.morestore.data.models.AddressType
import com.project.morestore.data.models.DeliveryAddress
import com.project.morestore.data.models.MyAddress
import com.project.morestore.util.inflater
import java.lang.StringBuilder

class MyAddressesAdapter(val callback :(MyAddress)->Unit) : RecyclerView.Adapter<MyAddressesAdapter.MyAddressHolder>(){
    private var items = arrayOf<MyAddress>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAddressHolder {
        return MyAddressHolder(ItemMyaddressBinding.inflate(parent.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: MyAddressHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems :Array<MyAddress>){
        items = newItems
        notifyDataSetChanged()
    }

    inner class MyAddressHolder(
        private val views : ItemMyaddressBinding
    ) :RecyclerView.ViewHolder(views.root){
        private lateinit var item :MyAddress
        init {
            itemView.setOnClickListener { callback.invoke(item) }
        }
        fun bind(myAddress : MyAddress){
            item = myAddress
            with(views){
                when(myAddress.type){
                    AddressType.CDEK.id -> {
                        icon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(views.root.context, R.color.green))
                        icon.setImageResource(R.drawable.ic_envelope)
                        title.setText(R.string.myAddress_delivery)
                    }
                    AddressType.HOME.id -> {
                        icon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(views.root.context, R.color.blue4))
                        icon.setImageResource(R.drawable.ic_package)
                        icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(views.root.context, R.color.white))
                        title.setText(R.string.myAddress_pickup)
                    }
                    AddressType.CDEK_POSTAMAT.id -> {
                        icon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(views.root.context, R.color.green))
                        icon.setImageResource(R.drawable.ic_envelope)
                        title.setText(R.string.cdek_postamat_delivery_address)
                    }
                }
                address.text = addressLine(myAddress.address)
                name.text = myAddress.name
                favorite.visibility = if(myAddress.favorite) View.VISIBLE else View.GONE
            }
        }

        private fun addressLine(address :DeliveryAddress) :String{
            val builder = StringBuilder("${address.city}, ${address.index}, ул. ${address.street}, дом ${address.house}")
            address.housing?.let { builder.append(", кр. $it") }
            address.building?.let { builder.append(", стр. $it") }
            address.apartment?.let { builder.append(", кв. $it") }
            return builder.toString()
        }
    }
}