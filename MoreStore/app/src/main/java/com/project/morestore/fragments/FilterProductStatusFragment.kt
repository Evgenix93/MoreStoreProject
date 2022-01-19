package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterProductStatusBinding
import com.project.morestore.singletones.FilterState

class FilterProductStatusFragment: Fragment(R.layout.fragment_filter_product_status) {
    private val binding: FragmentFilterProductStatusBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        initToolBar()
    }

    private fun bind(){

        if(FilterState.chosenProductStatus.isEmpty()){
            return
        }
        val allNotSelected = FilterState.chosenProductStatus.all { !it }
        binding.newWithTagCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenProductStatus[0]
        binding.newWithotuTagCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenProductStatus[1]
        binding.ExcellentCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenProductStatus[2]

    }
    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Показывать товары"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onStop() {
        super.onStop()
        FilterState.chosenProductStatus = listOf(binding.newWithTagCheckBox.isChecked, binding.newWithotuTagCheckBox.isChecked,
        binding.ExcellentCheckBox.isChecked)
    }
}