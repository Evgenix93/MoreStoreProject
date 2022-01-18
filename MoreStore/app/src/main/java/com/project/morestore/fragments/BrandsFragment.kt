package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.FilterState
import com.project.morestore.R
import com.project.morestore.adapters.BrandsAdapter
import com.project.morestore.adapters.CategoryAdapter
import com.project.morestore.databinding.FragmentBrandsBinding
import com.project.morestore.models.Category
import com.project.morestore.util.autoCleared

class BrandsFragment: Fragment(R.layout.fragment_brands) {
    private val binding: FragmentBrandsBinding by viewBinding()
    private var segmentsAdapter: CategoryAdapter by autoCleared()
    private var brands9Adapter: BrandsAdapter by autoCleared()
    private var brandsAAdapter: BrandsAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSegmentsRecyclerView()
        init0_9BrandsRecyclerView()
        initABrandsRecyclerView()
        loadFilter()
    }

    override fun onStop() {
        super.onStop()
        safeFilter()
    }

    private fun  initSegmentsRecyclerView(){
        segmentsAdapter = CategoryAdapter(false, requireContext()){_,_->}
        with(binding.segmentsRecyclerView){
            adapter = segmentsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    private fun init0_9BrandsRecyclerView(){
        brands9Adapter = BrandsAdapter(true)
        with(binding.brands09RecyclerView){
            adapter = brands9Adapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initABrandsRecyclerView(){
        brandsAAdapter = BrandsAdapter(false)
        with(binding.brandsARecyclerView){
            adapter = brandsAAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun safeFilter(){
        FilterState.segments = segmentsAdapter.segments
        FilterState.brands9 = brands9Adapter.brands9Checked
        FilterState.brandsA = brandsAAdapter.brandsAChecked
        FilterState.isAllBrands = segmentsAdapter.segments.all{it} && brands9Adapter.brands9Checked.all{it} && brandsAAdapter.brandsAChecked.all{it}
    }

    private fun loadFilter(){

        if(FilterState.segments.isNotEmpty()) {
            Log.d("Debug", "loadFilter")
            segmentsAdapter.segments = FilterState.segments.toMutableList()
            segmentsAdapter.notifyDataSetChanged()
        }

        if(FilterState.brands9.isNotEmpty()) {
            Log.d("Debug", "loadFilter brands9")
            brands9Adapter.brands9Checked = FilterState.brands9.toMutableList()
            brands9Adapter.notifyDataSetChanged()
        }
        if(FilterState.brandsA.isNotEmpty()){
            brandsAAdapter.brandsAChecked = FilterState.brandsA.toMutableList()
            brandsAAdapter.notifyDataSetChanged()
        }
    }
}