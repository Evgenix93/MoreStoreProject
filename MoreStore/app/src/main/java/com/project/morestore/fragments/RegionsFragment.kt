package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.FilterState
import com.project.morestore.R
import com.project.morestore.adapters.RegionsAdapter
import com.project.morestore.databinding.FragmentRegionsBinding
import com.project.morestore.util.autoCleared

class RegionsFragment: Fragment(R.layout.fragment_regions) {
   private val binding: FragmentRegionsBinding by viewBinding()
   private var regionsAdapter: RegionsAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRegionsRecyclerView()
        loadFilter()
    }

    override fun onStop() {
        super.onStop()
        safeFilter()
    }

    private fun initRegionsRecyclerView(){
        regionsAdapter = RegionsAdapter()
        binding.regionsRecyclerView.adapter = regionsAdapter
        binding.regionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


   private fun safeFilter(){
       Log.d("Debug", "safeFilter")
       Log.d("Debug", "${regionsAdapter.regionsChecked}")
        FilterState.regions = regionsAdapter.regionsChecked
    }
   private fun loadFilter(){
        if(FilterState.regions.isNotEmpty()) {
            Log.d("Debug", "loadFilter")
            regionsAdapter.regionsChecked = FilterState.regions.toMutableList()
            regionsAdapter.notifyDataSetChanged()
        }
    }
}