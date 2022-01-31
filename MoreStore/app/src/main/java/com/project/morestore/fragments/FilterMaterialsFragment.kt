package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.MaterialAdapter
import com.project.morestore.databinding.FragmentFilterMaterialsBinding
import com.project.morestore.models.MaterialLine
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared

class FilterMaterialsFragment: Fragment(R.layout.fragment_filter_materials) {
    private val binding: FragmentFilterMaterialsBinding by viewBinding()
    private var materialAdapter: MaterialAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
    }


    private fun initList(){
        materialAdapter = MaterialAdapter()
        with(binding.materialsList){
            adapter = materialAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        materialAdapter.updateList(generateList())



    }

    private fun generateList(): List<MaterialLine>{
        return if(FilterState.filter.chosenMaterials.isNotEmpty()){
            val allNotSelected = FilterState.filter.chosenMaterials.all { !it.isSelected }
           /* if(allNotSelected){
                for(material in FilterState.filter.chosenMaterials){
                    material.isSelected = true
                }
            }*/
            FilterState.filter.chosenMaterials
        } else listOf(
            MaterialLine(
            "Все материалы",
                false),
            MaterialLine("Акрил",
                false),
            MaterialLine("Альпака",
                false),
            MaterialLine("Ангора",
                false),
            MaterialLine("Атлас",
                false),
            MaterialLine("Ацетат",
                false),
            MaterialLine("Бархат",
                false),
            MaterialLine("Бисер",
                false),
            MaterialLine("Вельвет",
                false),
            MaterialLine("Велюр",
                false),
            MaterialLine("Вискоза",
                false)
        )
    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Материалы"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }



    override fun onStop() {
        super.onStop()
        FilterState.filter.chosenMaterials = materialAdapter.getCurrentMaterials()

    }


}