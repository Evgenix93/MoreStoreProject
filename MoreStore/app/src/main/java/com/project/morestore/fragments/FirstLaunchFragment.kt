package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.LoginDialog
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFirstLaunchBinding

class FirstLaunchFragment: Fragment(R.layout.fragment_first_launch) {
    private val binding: FragmentFirstLaunchBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()

    }


    private fun setClickListeners(){
        binding.createAccountBtn.setOnClickListener {
            LoginDialog().show(childFragmentManager, null)
        }
    }



}