package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOnboarding5Binding

class Onboarding5Fragment: Fragment(R.layout.fragment_onboarding5) {
    private val binding: FragmentOnboarding5Binding by viewBinding()
    private val args: Onboarding5FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.continueBtn.setOnClickListener{
            if (args.isMale)
            findNavController().navigate(
                Onboarding5FragmentDirections.actionOnboarding5FragmentToOnboarding6MaleFragment()
            )
            else
                findNavController().navigate(
                    Onboarding5FragmentDirections.actionOnboarding5FragmentToOnboarding6FemaleFragment()
                )
        }

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }
}