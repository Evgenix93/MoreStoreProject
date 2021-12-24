package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration3Binding

class Registration3Fragment: Fragment(R.layout.fragment_registration3) {
    private val binding: FragmentRegistration3Binding by viewBinding()
    private val args: Registration3FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
    }

    private fun setClickListeners(){
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(Registration3FragmentDirections.actionRegistration3FragmentToRegistration4Fragment(args.phoneOrEmail))
        }
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Личные данные"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }
}