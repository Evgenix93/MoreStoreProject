package com.project.morestore.fragments

import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.view.View
import androidx.core.text.toSpannable
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOrderCreateBinding
import com.project.morestore.dialogs.MenuBottomDialogDateFragment

class OrderCreateFragment: Fragment(R.layout.fragment_order_create) {
    private val binding: FragmentOrderCreateBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initRadioButtons()
        initViews()
    }


    private fun setClickListeners(){
        binding.dateEditText.setOnClickListener {
            MenuBottomDialogDateFragment(requireContext(), false, { day, month, year ->
                binding.dateEditText.setText("$day.$month.$year")

            }, { _,_ ->

            }).show(childFragmentManager, null)
        }

        binding.timeEditText.setOnClickListener {
            MenuBottomDialogDateFragment(requireContext(), true, { _, _ ,_ ->

            }, { hour, minute ->
                binding.timeEditText.setText("$hour:$minute")


            }).show(childFragmentManager, null)
        }

    }

    private fun initRadioButtons(){
        binding.radioButtons.setOnCheckedChangeListener { radioGroup, id ->
            binding.chooseOnMapTextView.isVisible = id == R.id.userVariantRadioBtn
            binding.placeIcon.isVisible = id == R.id.userVariantRadioBtn
            binding.whenReceiveTextView.isVisible = id == R.id.userVariantRadioBtn
            binding.pickers.isVisible = id == R.id.userVariantRadioBtn



        }

    }

    private fun initViews(){
        val oldPriceStr = binding.oldPriceTextView.text.toSpannable().apply { setSpan(StrikethroughSpan(), 0, length, 0) }
        binding.oldPriceTextView.text = oldPriceStr
    }


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