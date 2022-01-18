package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterProductStatusBinding
import com.project.morestore.singletones.FilterState

class FilterProductStatusFragment: Fragment(R.layout.fragment_filter_product_status) {
    private val binding: FragmentFilterProductStatusBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    private fun bind(){
        if(FilterState.chosenProductStatus.isEmpty()){
            return
        }
        binding.newWithTagCheckBox.isChecked = FilterState.chosenProductStatus[0]
        binding.newWithotuTagCheckBox.isChecked = FilterState.chosenProductStatus[1]
        binding.ExcellentCheckBox.isChecked = FilterState.chosenProductStatus[2]

    }

    override fun onDestroyView() {
        super.onDestroyView()
        FilterState.chosenProductStatus = listOf(binding.newWithTagCheckBox.isChecked, binding.newWithotuTagCheckBox.isChecked,
        binding.ExcellentCheckBox.isChecked)
    }
}