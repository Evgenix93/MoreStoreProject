package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentRegistration2Binding

class Registration2Fragment: Fragment(R.layout.fragment_registration2) {
    private val binding: FragmentRegistration2Binding by viewBinding()
    private val args: Registration2FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        initToolbar()

        if(args.phoneOrEmail.contains(Regex("[a-z]"))){
            binding.textView2.text = "В течении 2 минут вы получите письмо с кодом"
            binding.textView3.text = "подтверждения на почту"
            binding.phoneEmailTextView.text = "irina.ivanova@mail.ru"
        }
    }

    private fun setClickListeners(){
        binding.confirmBtn.setOnClickListener {
            //findNavController().previousBackStackEntry.destination.id
            findNavController().navigate(Registration2FragmentDirections.actionRegistration2FragmentToRegistration3Fragment(args.phoneOrEmail))
        }


    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Подтвержденин номера"
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }
}