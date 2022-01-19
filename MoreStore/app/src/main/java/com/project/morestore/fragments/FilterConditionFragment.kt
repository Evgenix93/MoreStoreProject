package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterConditionBinding
import com.project.morestore.singletones.FilterState

class FilterConditionFragment: Fragment(R.layout.fragment_filter_condition) {
    private val binding: FragmentFilterConditionBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        initToolBar()
    }

    private fun bind(){
        if(FilterState.chosenConditions.isEmpty()){
            return
        }
        val allNotSelected = FilterState.chosenConditions.all { !it }
        binding.newWithTagCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenConditions[0]
        binding.newWithotuTagCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenConditions[1]
        binding.ExcellentCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenConditions[2]
        binding.goodCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenConditions[3]
    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Состояние"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onStop() {
        super.onStop()
        FilterState.chosenConditions = listOf(binding.newWithTagCheckBox.isChecked, binding.newWithotuTagCheckBox.isChecked,
        binding.ExcellentCheckBox.isChecked, binding.goodCheckBox.isChecked)
    }
}