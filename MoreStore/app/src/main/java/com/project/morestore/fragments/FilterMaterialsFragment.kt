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
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterMaterialsFragment: MvpAppCompatFragment(R.layout.fragment_filter_materials), UserMvpView {
    private val binding: FragmentFilterMaterialsBinding by viewBinding()
    private var materialAdapter: MaterialAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val defaultMaterials = generateList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        loadFilterMaterials()
    }


    private fun initList(){
        materialAdapter = MaterialAdapter()
        with(binding.materialsList){
            adapter = materialAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        materialAdapter.updateList(defaultMaterials)



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

    private fun loadFilterMaterials(){
        presenter.loadMaterials()
    }



    override fun onStop() {
        super.onStop()
        presenter.saveMaterials(materialAdapter.getCurrentMaterials())

    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        materialAdapter.updateList(result as List<MaterialLine>)

    }

    override fun successNewCode() {

    }


}