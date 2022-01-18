package com.project.morestore.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.FilterState
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFilterBinding
import kotlinx.coroutines.launch

class FilterFragment: Fragment(R.layout.fragment_filter) {
    private val binding: FragmentFilterBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showGreenDots()
        setClickListeners()
        initAutoCompleteTextView()
    }

    private fun showGreenDots(){
        binding.categoriesGreenDotImageView.isVisible = !FilterState.isAllCategories
        binding.allCategories.isVisible = FilterState.isAllCategories
        Log.d("Debug", "showGreenDots")
       /* viewLifecycleOwner.lifecycleScope.launch{
            val sharedPrefs = requireContext().getSharedPreferences("categories_shared_prefs", Context.MODE_PRIVATE)
            val isAllCategories = sharedPrefs.getBoolean("all_categories", true)
            Log.d("Debug", "isAllCategories = $isAllCategories")
            binding.categoriesGreenDotImageView.isVisible = !isAllCategories
            binding.allCategories.isVisible = isAllCategories
        }*/
        Log.d("Debug", "isAllBrands = ${FilterState.isAllBrands}")
        binding.brandsGreenDotImageView.isVisible = !FilterState.isAllBrands
        binding.allBrands.isVisible = FilterState.isAllBrands
        binding.regionsGreenDotImageView.isVisible = !FilterState.regions.all { it }
        binding.allRegions.isVisible = FilterState.regions.all { it }
    }

    private fun setClickListeners(){
        binding.categoriesTextView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToCategoriesFragment())
        }
        binding.brandTextView.setOnClickListener{
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToBrandsFragment())
        }
        binding.searchRegionTextView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToRegionsFragment())
        }
    }

    private fun initAutoCompleteTextView(){
        val types = resources.getStringArray(R.array.type_array)
        ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, types).also { adapter ->
            binding.typeAutoCompleteTextView.setAdapter(adapter)
        }
    }
}