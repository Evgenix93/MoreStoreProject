package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentSuccessPromoteBinding

class SuccessPromoteFragment: Fragment(R.layout.fragment_success_promote) {
    private val binding: FragmentSuccessPromoteBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
    }


    private fun initToolbar(){
        with(binding.toolbar){
            titleTextView.text = "Получите больше просмотров"
            backIcon.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}