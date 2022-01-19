package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterStyleBinding
import com.project.morestore.singletones.FilterState

class FilterStyleFragment : Fragment(R.layout.fragment_filter_style) {
    private val binding: FragmentFilterStyleBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
        initToolBar()

    }

    private fun bind() {
        if (FilterState.chosenStyles.isEmpty()) {
            return
        } else {
            val allNotSelected = FilterState.chosenStyles.all { !it }
            binding.newWithTagCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenStyles[0]
            binding.newWithotuTagCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenStyles[1]
            binding.ExcellentCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenStyles[2]
            binding.goodCheckBox.isChecked = if(allNotSelected) true else FilterState.chosenStyles[3]
        }
    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Стиль"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onStop() {
        super.onStop()
        FilterState.chosenStyles = listOf(
            binding.newWithTagCheckBox.isChecked,
            binding.newWithotuTagCheckBox.isChecked,
            binding.ExcellentCheckBox.isChecked,
            binding.goodCheckBox.isChecked
        )
    }
}