package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration1Binding

class Registration4Fragment : Fragment(R.layout.fragment_registration1) {
    private val binding: FragmentRegistration1Binding by viewBinding()
    private val args: Registration4FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
        binding.registerTextView.text =
            if (!args.phoneOrEmail.contains(Regex("[a-z]")) || args.phoneOrEmail.isEmpty()) {
                "Почта"
            } else {
                "Телефон"
            }
    }

    private fun setClickListeners() {
        binding.getCodeBtn.setOnClickListener {
            findNavController().navigate(
                Registration4FragmentDirections.actionRegistration4FragmentToRegistration2Fragment(
                    binding.phoneEmailEditText.text.toString()
                )
            )
        }
    }

    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Последний шаг"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

}