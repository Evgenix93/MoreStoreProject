package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOnboarding2Binding

class Onboarding2Fragment: Fragment(R.layout.fragment_onboarding2) {
    private val binding: FragmentOnboarding2Binding by viewBinding()
    private val args: Onboarding2FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.continueBtn.setOnClickListener{
            findNavController().navigate(
                Onboarding2FragmentDirections.actionOnboarding2FragmentToOnboarding3Fragment(
                  args.isMale
                )
            )
        }
    }
}