package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.text.style.StrikethroughSpan
import android.view.View
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentPriceBinding

class PriceFragment: Fragment(R.layout.fragment_price){
    private val binding: FragmentPriceBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews(){
        if(arguments?.getBoolean(FOR_ONE_DAY) == true){
            binding.oldPriceTextView.text = "199 ₽".toSpannable().apply {
                setSpan(StrikethroughSpan(), 0, length, 0) }
            binding.newPriceTextView.text = "99 ₽"
        }else{
            binding.oldPriceTextView.text = "499 ₽".toSpannable().apply {
                setSpan(StrikethroughSpan(), 0, length, 0) }
            binding.newPriceTextView.text = "219 ₽"
        }

    }


    companion object {
        const val FOR_ONE_DAY = "for one day"
    }
}