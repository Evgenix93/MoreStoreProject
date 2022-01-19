package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentFilterSizesColthesBinding
import com.project.morestore.models.SizeLine
import com.project.morestore.singletones.FilterState

class FilterSizesFragment: Fragment(R.layout.fragment_filter_sizes_colthes) {
    private val binding: FragmentFilterSizesColthesBinding by viewBinding()
    private val sizeAdapter = SizeLineAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
    }


    private fun initList(){
        with(binding.sizesList){
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        val sizeList = listOf(
            SizeLine(
                "XXS",
                "26-27",
                "42",
                "32",
                "32",
                true
            ),
            SizeLine(
                "XS",
                "28-29",
                "44",
                "34",
                "34",
                true
            ),
            SizeLine(
                "S",
                "30-31",
                "46",
                "36",
                "36",
                true
            ),
            SizeLine(
                "M",
                "32-33",
                "48",
                "38",
                "38",
                true
            ),
            SizeLine(
                "L",
                "34-35",
                "50",
                "40",
                "40",
                true
            ),
            SizeLine(
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                true
            ),
            SizeLine(
                "XXL",
                "38-39",
                "54",
                "44",
                "44",
                true
            ),
            SizeLine(
                "3XL",
                "40-41",
                "56",
                "46",
                "46",
                true
            ),
            SizeLine(
                "4XL",
                "42-43",
                "58",
                "48",
                "48",
                true
            ),
            SizeLine(
                "5XL",
                "44-45",
                "60",
                "50",
                "60",
                true
            ),
            SizeLine(
                "",
                "",
                "",
                "",
                "",
                true
            )


        )

        sizeAdapter.updateList(if(FilterState.chosenSizes.isNotEmpty()){
            val allNotSelected = FilterState.chosenSizes.all { !it.isSelected }
            if(allNotSelected){
                for(size in FilterState.chosenSizes){
                    size.isSelected = true
                }
            }
            FilterState.chosenSizes
        } else sizeList)
    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Размер"
        binding.toolbar.actionTextView.text = "Сбросить"
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onStop() {
        super.onStop()
        FilterState.chosenSizes = sizeAdapter.getChosenSizes()
    }
}