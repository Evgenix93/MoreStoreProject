package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentAddCardBinding
import com.redmadrobot.inputmask.MaskedTextChangedListener

class AddCardFragment: Fragment(R.layout.fragment_add_card) {
    private val binding: FragmentAddCardBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditTexts()
        initToolbar()
    }


    private fun initEditTexts(){
        val cardNumberListener = MaskedTextChangedListener("[0000]-[0000]-[0000]-[0000]", binding.editText)
        binding.editText.addTextChangedListener(cardNumberListener)
        val cardDateListener = MaskedTextChangedListener("[00]/[00]", binding.editText4)
        binding.editText4.addTextChangedListener(cardDateListener)
        val cardCvvListener = MaskedTextChangedListener("[000]", binding.editText2)
        binding.editText2.addTextChangedListener(cardCvvListener)
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.isVisible = false
    }
}