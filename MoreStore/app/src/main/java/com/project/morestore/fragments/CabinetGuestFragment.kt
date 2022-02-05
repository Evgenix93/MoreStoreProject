package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCabinetGuestBinding

class CabinetGuestFragment: Fragment(R.layout.fragment_cabinet_guest) {
    private val binding: FragmentCabinetGuestBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners()
        showBottomNav()
    }



    private fun setClickListeners(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(CabinetGuestFragmentDirections.actionCabinetGuestFragmentToLoginDialog())
        }
    }

    private fun showBottomNav(){
        (activity as MainActivity).showBottomNavBar(true)
    }
}