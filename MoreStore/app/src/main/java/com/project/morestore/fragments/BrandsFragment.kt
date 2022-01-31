package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.BrandsAdapter
import com.project.morestore.adapters.CategoryAdapter
import com.project.morestore.databinding.FragmentBrandsBinding
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared

class BrandsFragment : Fragment(R.layout.fragment_brands) {
    private val binding: FragmentBrandsBinding by viewBinding()
    private var segmentsAdapter: CategoryAdapter by autoCleared()
    private var brands9Adapter: BrandsAdapter by autoCleared()
    private var brandsAAdapter: BrandsAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSegmentsRecyclerView()
        init0_9BrandsRecyclerView()
        initABrandsRecyclerView()
        loadFilter()
    }

    override fun onStop() {
        super.onStop()
        safeFilter()
    }

    private fun initSegmentsRecyclerView() {
        segmentsAdapter = CategoryAdapter(false, requireContext()) { _, _ -> }
        with(binding.segmentsRecyclerView) {
            adapter = segmentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun init0_9BrandsRecyclerView() {
        brands9Adapter = BrandsAdapter(true)
        with(binding.brands09RecyclerView) {
            adapter = brands9Adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initABrandsRecyclerView() {
        brandsAAdapter = BrandsAdapter(false)
        with(binding.brandsARecyclerView) {
            adapter = brandsAAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun safeFilter() {
        FilterState.filter.segments = segmentsAdapter.loadSegments2Checked()
        FilterState.filter.brands9 = brands9Adapter.loadBrands9Checked()
        FilterState.filter.brandsA = brandsAAdapter.loadBrandsAChecked()
        FilterState.filter.isAllBrands = (segmentsAdapter.loadSegments2Checked()
            .all { !it } && brands9Adapter.loadBrands9Checked().all { !it } && brandsAAdapter.loadBrandsAChecked().all { !it }) || (segmentsAdapter.loadSegments2Checked()
            .all { it } && brands9Adapter.loadBrands9Checked().all { it } && brandsAAdapter.loadBrandsAChecked().all { it })
    }

    private fun loadFilter() {

        if (FilterState.filter.segments.isNotEmpty()) {
            Log.d("Debug", "loadFilter")
            segmentsAdapter.updateSegmentsChecked(FilterState.filter.segments.toMutableList())
        }

        if (FilterState.filter.brands9.isNotEmpty()) {
            Log.d("Debug", "loadFilter brands9")
            brands9Adapter.updateBrands9Checked(FilterState.filter.brands9.toMutableList())
        }
        if (FilterState.filter.brandsA.isNotEmpty()) {
            brandsAAdapter.updateBrandsAChecked(FilterState.filter.brandsA.toMutableList())
        }
    }

    private fun initToolbar() {
        binding.toolbarFilter.titleTextView.text = "Бренд или сегмент"
        binding.toolbarFilter.actionTextView.text = "Сбросить"
        binding.toolbarFilter.imageView2.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}