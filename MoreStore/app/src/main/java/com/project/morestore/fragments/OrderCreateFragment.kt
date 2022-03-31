package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOrderCreateBinding

class OrderCreateFragment: Fragment(R.layout.fragment_order_create) {
    private val binding: FragmentOrderCreateBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRadioButtons()
    }

    private fun initRadioButtons(){
       binding.deliveryTypeRadioGroup.setOnCheckedChangeListener { _, radioButton ->
           binding.prepaymentInfoTextView.isVisible = radioButton == R.id.prepaymentRadioButton
           binding.totalCardView.isVisible = radioButton == R.id.prepaymentRadioButton
           if(radioButton == R.id.prepaymentRadioButton)
               binding.payButton.text = "Оплатить заказ"
       }
    }
}