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

class FilterFragment : Fragment(R.layout.fragment_filter) {
    private val binding: FragmentFilterBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showGreenDots()
        setClickListeners()
        initAutoCompleteTextView()
        initToolBar()
    }

    private fun showGreenDots() {
        if (com.project.morestore.singletones.FilterState.chosenForWho.isNotEmpty()) {
            binding.forWomenTextView.text =
                if (com.project.morestore.singletones.FilterState.chosenForWho[0]) "Женщинам"
                else if (com.project.morestore.singletones.FilterState.chosenForWho[1]) "Мужчинам"
                else
                    "Детям"
        }

        binding.showProductsGreenDotImageView.isVisible =
            if (com.project.morestore.singletones.FilterState.chosenProductStatus.isEmpty()) true
            else (com.project.morestore.singletones.FilterState.chosenProductStatus.all { it } || com.project.morestore.singletones.FilterState.chosenProductStatus.all { !it }).not()

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
        binding.regionsGreenDotImageView.isVisible = !FilterState.regions.all { it.isChecked == true }
        binding.allRegions.isVisible = FilterState.regions.all { it.isChecked == true }
        //binding.showProductsGreenDotImageView.isVisible = com.project.morestore.singletones.FilterState.chosenProductStatus
        //val allSelected = com.project.morestore.singletones.FilterState.chosenStyles.all { it } || com.project.morestore.singletones.FilterState.chosenStyles.all { !it }
        binding.stylesGreenDotImageView.isVisible =
            (com.project.morestore.singletones.FilterState.chosenStyles.all { it } || com.project.morestore.singletones.FilterState.chosenStyles.all { !it }).not()
        binding.allStyles.isVisible =
            com.project.morestore.singletones.FilterState.chosenStyles.all { it } || com.project.morestore.singletones.FilterState.chosenStyles.all { !it }
        binding.conditionsGreenDotImageView.isVisible =
            (com.project.morestore.singletones.FilterState.chosenConditions.all { it } || com.project.morestore.singletones.FilterState.chosenConditions.all { !it }).not()
        binding.allConditions.isVisible =
            com.project.morestore.singletones.FilterState.chosenConditions.all { it } || com.project.morestore.singletones.FilterState.chosenConditions.all { !it }
        binding.sizesGreenDotImageView.isVisible =
            (com.project.morestore.singletones.FilterState.chosenSizes.all { it.isSelected } || com.project.morestore.singletones.FilterState.chosenSizes.all { !it.isSelected }).not()
        binding.allSizes.isVisible =
            com.project.morestore.singletones.FilterState.chosenSizes.all { it.isSelected } || com.project.morestore.singletones.FilterState.chosenSizes.all { !it.isSelected }
        binding.materialsGreenDotImageView.isVisible =
            (com.project.morestore.singletones.FilterState.chosenMaterials.all { it.isSelected } || com.project.morestore.singletones.FilterState.chosenMaterials.all { !it.isSelected }).not()
        binding.allMaterials.isVisible =
            com.project.morestore.singletones.FilterState.chosenMaterials.all { it.isSelected } || com.project.morestore.singletones.FilterState.chosenMaterials.all { !it.isSelected }
    }

    private fun setClickListeners() {
        binding.categoryClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToCategoriesFragment())
        }
        binding.brandClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToBrandsFragment())
        }
        binding.regionClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToRegionsFragment())
        }

        binding.colorClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToColorsFragment())
        }

        binding.forWhoClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterForWhoFragment())
        }

        binding.showProductsClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterProductStatusFragment())
        }

        binding.stylesClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterStyleFragment())
        }

        binding.conditionClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterConditionFragment())
        }

        binding.sizeClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterSizesFragment())
        }

        binding.materialsClickView.setOnClickListener {
            findNavController().navigate(FilterFragmentDirections.actionFilterFragmentToFilterMaterialsFragment())
        }


    }

    private fun initAutoCompleteTextView() {
        val types = resources.getStringArray(R.array.type_array)
        ArrayAdapter(requireContext(), R.layout.item_suggestion_textview, types).also { adapter ->
            binding.typeAutoCompleteTextView.setAdapter(adapter)
        }
        binding.typeAutoCompleteTextView.setOnClickListener { binding.typeAutoCompleteTextView.showDropDown() }

    }

    private fun initToolBar(){
        binding.toolbarFilter.imageView2.setOnClickListener { findNavController().popBackStack() }
    }
}