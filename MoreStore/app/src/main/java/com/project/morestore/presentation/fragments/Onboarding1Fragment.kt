package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentOnboarding1Binding

class Onboarding1Fragment : Fragment(R.layout.fragment_onboarding1) {
    private val binding: FragmentOnboarding1Binding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        hideBottomNavBar()

    }

    private fun setClickListeners(){
        binding.forMaleBtn.setOnClickListener {
            findNavController().navigate(
                Onboarding1FragmentDirections.actionOnboarding1FragmentToOnboarding2Fragment(
                    true
                )
            )
        }
        binding.forFemaleBtn.setOnClickListener{
            findNavController().navigate(
                Onboarding1FragmentDirections.actionOnboarding1FragmentToOnboarding2Fragment(
                    false
                )
            )
        }
    }

    private fun hideBottomNavBar(){
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(false)
    }
}