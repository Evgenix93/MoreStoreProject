package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.morestore.R
import com.project.morestore.adapters.MyAdapter
import com.project.morestore.databinding.FragmentOnboarding4Binding

class Onboarding4Fragment: Fragment(R.layout.fragment_onboarding4) {
    private val binding: FragmentOnboarding4Binding by viewBinding()
    private val args: Onboarding4FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.infoViewPager.adapter = MyAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.infoViewPager){tab,position ->
            when (position){
                0 -> tab.text = getString(R.string.to_buyers)
                1 -> tab.text = getString(R.string.to_sellers)
            }
        }.attach()
        binding.continueBtn.setOnClickListener{
            findNavController().navigate(
                Onboarding4FragmentDirections.actionOnboarding4FragmentToOnboarding5Fragment(
                    args.isMale
                )
            )
        }

        binding.backIcon.setOnClickListener { findNavController().popBackStack() }


    }
}