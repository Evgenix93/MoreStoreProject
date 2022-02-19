package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentHowSellInfoBinding

class CreateProductHowToSellFragment: Fragment(R.layout.fragment_how_sell_info) {
    private val binding: FragmentHowSellInfoBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
    }


    private fun setClickListeners(){
        binding.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}