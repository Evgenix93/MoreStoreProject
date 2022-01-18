package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterConditionBinding
import com.project.morestore.singletones.FilterState

class FilterConditionFragment: Fragment(R.layout.fragment_filter_condition) {
    private val binding: FragmentFilterConditionBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    private fun bind(){
        if(FilterState.chosenConditions.isEmpty()){
            return
        }
        binding.newWithTagCheckBox.isChecked = FilterState.chosenConditions[0]
        binding.newWithotuTagCheckBox.isChecked = FilterState.chosenConditions[1]
        binding.ExcellentCheckBox.isChecked = FilterState.chosenConditions[2]
        binding.goodCheckBox.isChecked = FilterState.chosenConditions[3]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FilterState.chosenConditions = listOf(binding.newWithTagCheckBox.isChecked, binding.newWithotuTagCheckBox.isChecked,
        binding.ExcellentCheckBox.isChecked, binding.goodCheckBox.isChecked)
    }
}