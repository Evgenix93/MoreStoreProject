package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
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
        return if(FilterState.chosenMaterials.isNotEmpty()) FilterState.chosenMaterials else listOf(
            MaterialLine(
            "Все материалы",
                true),
            MaterialLine("Акрил",
                true),
            MaterialLine("Альпака",
                true),
            MaterialLine("Ангора",
                true),
            MaterialLine("Атлас",
                true),
            MaterialLine("Ацетат",
                true),
            MaterialLine("Бархат",
                true),
            MaterialLine("Бисер",
                true),
            MaterialLine("Вельвет",
                true),
            MaterialLine("Велюр",
                true),
            MaterialLine("Вискоза",
                true)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FilterState.chosenMaterials = materialAdapter.getChosenMaterials()
    }
}