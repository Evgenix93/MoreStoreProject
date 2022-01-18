package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.checkbox.MaterialCheckBox
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterForWhoBinding
import com.project.morestore.singletones.FilterState

class FilterForWhoFragment : Fragment(R.layout.fragment_filter_for_who) {
    private val binding: FragmentFilterForWhoBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRadioButtons()
    }

    private fun initRadioButtons() {
        binding.forWomenCheckBox.setOnClickListener {
            setupRadioButton(binding.forWomenCheckBox)
        }
        binding.forMenCheckBox.setOnClickListener {
            setupRadioButton(binding.forMenCheckBox)
        }
        binding.forKidsCheckBox.setOnClickListener {
            setupRadioButton(binding.forKidsCheckBox)
        }
    }

    private fun setupRadioButton(radioBtn: MaterialCheckBox) {
        if (!radioBtn.isChecked) {
            radioBtn.isChecked = true
            return
        }
        binding.forWomenCheckBox.isChecked = false
        binding.forMenCheckBox.isChecked = false
        binding.forKidsCheckBox.isChecked = false
        radioBtn.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FilterState.chosenForWho = listOf(
            binding.forWomenCheckBox.isChecked,
            binding.forMenCheckBox.isChecked,
            binding.forKidsCheckBox.isChecked
        )
    }
}