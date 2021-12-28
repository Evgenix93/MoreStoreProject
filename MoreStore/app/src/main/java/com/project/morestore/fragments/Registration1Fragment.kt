package com.project.morestore.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration1Binding

class Registration1Fragment: Fragment(R.layout.fragment_registration1)  {
    private val binding: FragmentRegistration1Binding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()
    }

    private fun setClickListeners(){
        binding.getCodeBtn.setOnClickListener {
            findNavController().navigate(Registration1FragmentDirections.actionRegistration1FragmentToRegistration2Fragment(binding.phoneEmailEditText.text.toString()))

        }

    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initKeyBoard(){
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.phoneEmailEditText.setOnEditorActionListener { textView, i, keyEvent ->
            true
        }

    }
}