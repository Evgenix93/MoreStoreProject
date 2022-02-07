package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.MaterialAdapter
import com.project.morestore.databinding.FragmentFilterMaterialsBinding
import com.project.morestore.models.Filter
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        loadFilter()
    }


    private fun initList(){
        materialAdapter = MaterialAdapter()
        with(binding.materialsList){
            adapter = materialAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }




    }

    private fun generateList(): List<MaterialLine>{
        return listOf(
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

    private fun loadFilter(){
        presenter.getFilter()
    }

    private fun saveMaterials(){
        presenter.saveMaterials(materialAdapter.getCurrentMaterials())
    }

    private fun bindFilter(filter: Filter){
        if(filter.chosenMaterials.isNotEmpty()){
            materialAdapter.updateList(filter.chosenMaterials)
        }else{
            materialAdapter.updateList(generateList())
        }
    }



    override fun onStop() {
        super.onStop()
        saveMaterials()

    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        when (result){
            is Filter -> bindFilter(result)
        }

    }

    override fun successNewCode() {

    }


}