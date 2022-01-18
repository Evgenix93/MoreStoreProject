package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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
                false
            ),
            SizeLine(
                "XS",
                "28-29",
                "44",
                "34",
                "34",
                false
            ),
            SizeLine(
                "S",
                "30-31",
                "46",
                "36",
                "36",
                false
            ),
            SizeLine(
                "M",
                "32-33",
                "48",
                "38",
                "38",
                false
            ),
            SizeLine(
                "L",
                "34-35",
                "50",
                "40",
                "40",
                false
            ),
            SizeLine(
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false
            ),
            SizeLine(
                "XXL",
                "38-39",
                "54",
                "44",
                "44",
                false
            ),
            SizeLine(
                "3XL",
                "40-41",
                "56",
                "46",
                "46",
                false
            ),
            SizeLine(
                "4XL",
                "42-43",
                "58",
                "48",
                "48",
                false
            ),
            SizeLine(
                "5XL",
                "44-45",
                "60",
                "50",
                "60",
                false
            ),
            SizeLine(
                "",
                "",
                "",
                "",
                "",
                false
            )


        )

        sizeAdapter.updateList(if(FilterState.chosenSizes.isNotEmpty()) FilterState.chosenSizes else sizeList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FilterState.chosenSizes = sizeAdapter.getChosenSizes()
    }
}