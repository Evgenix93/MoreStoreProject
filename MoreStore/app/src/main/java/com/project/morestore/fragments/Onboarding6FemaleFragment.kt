package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFirstLaunchBinding

class Onboarding6FemaleFragment: Fragment(R.layout.fragment_first_launch) {
    private val binding: FragmentFirstLaunchBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initText()
        setClickListeners()

    }

    private fun setClickListeners(){
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initText(){
        binding.createAccountBtn.text = "Авторизоваться"
    }

}