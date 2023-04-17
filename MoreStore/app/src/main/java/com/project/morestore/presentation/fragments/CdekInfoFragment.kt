package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.CdekInfoFragmentBinding

class CdekInfoFragment: Fragment(R.layout.cdek_info_fragment) {
    private val binding: CdekInfoFragmentBinding by viewBinding()
    private val args: CdekInfoFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.titleTextView.text = "Информация по СДЭК заказу"
        binding.toolbar.actionIcon.isVisible = false
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
        binding.infoTextView.text = args.cdekInfo.toString()
    }

}