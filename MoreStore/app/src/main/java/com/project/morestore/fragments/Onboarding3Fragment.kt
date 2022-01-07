package com.project.morestore.fragments

import android.content.Context
import android.graphics.MaskFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOnboarding3Binding
import com.project.morestore.repositories.ProductRepository

class Onboarding3Fragment: Fragment(R.layout.fragment_onboarding3) {
    private val binding: FragmentOnboarding3Binding by viewBinding()
    private val args: Onboarding3FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.continueBtn.setOnClickListener{
            findNavController().navigate(
                Onboarding3FragmentDirections.actionOnboarding3FragmentToOnboarding4Fragment(
                    args.isMale
                )
            )
        }

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
        binding.allCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.luxuryCheckBox.isChecked = isChecked
            binding.middleCheckBox.isChecked = isChecked
            binding.massCheckBox.isChecked = isChecked
            binding.economyCheckBox.isChecked = isChecked
        }
    }
}