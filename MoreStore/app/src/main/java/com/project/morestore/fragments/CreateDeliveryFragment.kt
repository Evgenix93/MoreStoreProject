package com.project.morestore.fragments

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentDeliveryCreateBinding
import com.project.morestore.fragments.orders.create.OrderCreateFragment
import com.project.morestore.fragments.orders.create.OrderCreateFragmentDirections
import com.project.morestore.models.MyAddress

class CreateDeliveryFragment: Fragment(R.layout.fragment_delivery_create) {
    private val binding: FragmentDeliveryCreateBinding by viewBinding()

    fun setClickListeners(){
        binding.chooseAddressButton.setOnClickListener{

        }
    }

    private fun getChosenAddress(onAddressReceived: (addressName: String, address: String, type: Int) -> Unit){

        setFragmentResultListener(MyAddressesFragment.ADDRESS_REQUEST){_, bundle ->
            val address = bundle.getParcelable<MyAddress>(MyAddressesFragment.ADDRESS_KEY)
            Log.d("mylog", "address $address")
          //  chosenAddress = address
            val streetStr = address?.address?.street
            val houseStr = if(address?.address?.house != null) "дом.${address.address.house}" else null
            val housingStr = if(address?.address?.housing != null) "кп.${address.address.housing}" else null
            val buildingStr = if(address?.address?.building != null) "стр.${address.address.building}" else null
            val apartmentStr = if(address?.address?.apartment != null) "кв.${address.address.apartment}" else null
            val strings =
                arrayOf(streetStr, houseStr, housingStr, buildingStr, apartmentStr).filterNotNull()
            binding.addressTextView.text = strings.joinToString(", ")
           // chosenAddressStr = strings.joinToString(", ")
            onAddressReceived(address?.name.orEmpty(), strings.joinToString(", "), address?.type ?: 0)
        }

        findNavController().navigate(
            OrderCreateFragmentDirections
            .actionCreateOrderFragmentToMyAddressesFragment(true,
                MyAddressesFragment.ADDRESSES_ALL))

    }
}