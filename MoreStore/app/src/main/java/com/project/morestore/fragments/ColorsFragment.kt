package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentColorsBinding

class ColorsFragment: Fragment(R.layout.fragment_colors) {
    private val binding:FragmentColorsBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initCheckBox()
    }

    private fun initToolbar(){
        binding.toolbarFilter.titleTextView.text = "Цвет"
        binding.toolbarFilter.actionTextView.isVisible = false
        binding.toolbarFilter.imageView2.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun initCheckBox(){
       binding.allColorsCheckBox.isChecked = true
    }
}