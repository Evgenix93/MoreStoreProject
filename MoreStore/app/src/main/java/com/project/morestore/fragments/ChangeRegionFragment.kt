package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.SuggestionArrayAdapter
import com.project.morestore.databinding.FragmentChangeRegionBinding

class ChangeRegionFragment : Fragment(R.layout.fragment_change_region) {
    private val binding: FragmentChangeRegionBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initEditText()







        binding.crossIcon.setOnClickListener {
            binding.searchEditText.setText("")
        }

        binding.view12.setOnClickListener { findNavController().navigate(ChangeRegionFragmentDirections.actionChangeRegionFragmentToAutoLocationFragment()) }
    }

    private fun initToolbar(){
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initEditText(){
        binding.searchEditText.setAdapter(
            SuggestionArrayAdapter(
                requireContext(),
                R.layout.item_suggestion_textview,
                listOf("Саранск","Саров", "Саратов", "Сарапул ")

            )
        )
        binding.searchEditText.setOnFocusChangeListener { view, focused ->
            (activity as MainActivity).showBottomNavBar(!focused)
        }

    }
}