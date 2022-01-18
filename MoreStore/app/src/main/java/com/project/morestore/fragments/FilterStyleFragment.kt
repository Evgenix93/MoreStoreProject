package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterStyleBinding
import com.project.morestore.singletones.FilterState

class FilterStyleFragment : Fragment(R.layout.fragment_filter_style) {
    private val binding: FragmentFilterStyleBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    private fun bind() {
        if (FilterState.chosenStyles.isEmpty()) {
            return
        } else {
            binding.newWithTagCheckBox.isChecked = FilterState.chosenStyles[0]
            binding.newWithotuTagCheckBox.isChecked = FilterState.chosenStyles[1]
            binding.ExcellentCheckBox.isChecked = FilterState.chosenStyles[2]
            binding.goodCheckBox.isChecked = FilterState.chosenStyles[3]
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FilterState.chosenStyles = listOf(
            binding.newWithTagCheckBox.isChecked,
            binding.newWithotuTagCheckBox.isChecked,
            binding.ExcellentCheckBox.isChecked,
            binding.goodCheckBox.isChecked
        )
    }
}