package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.RegionsAdapter
import com.project.morestore.databinding.FragmentRegionsBinding
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared

class RegionsFragment: Fragment(R.layout.fragment_regions) {
   private val binding: FragmentRegionsBinding by viewBinding()
   private var regionsAdapter: RegionsAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
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
       //Log.d("Debug", "${regionsAdapter.regionsChecked}")
         val regions = regionsAdapter.getCurrentRegions()
       /* if (regions.all{ !it.isChecked })
            regions.forEachIndexed{index,_->
                regions[index].isChecked = true
            }*/
        FilterState.filter.regions = regions
    }
   private fun loadFilter(){
        if(FilterState.filter.regions.isNotEmpty()) {
            Log.d("Debug", "loadFilter")
            regionsAdapter.updateList(FilterState.filter.regions)

        }
    }

    private fun initToolbar(){
        binding.toolbarFilter.titleTextView.text = "Регион поиска"
        binding.toolbarFilter.actionTextView.isVisible = false
        binding.toolbarFilter.imageView2.setOnClickListener{
            findNavController().popBackStack()
        }
    }
}