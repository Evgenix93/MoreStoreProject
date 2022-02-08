package com.project.morestore.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.project.morestore.models.Property
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterMaterialsFragment: MvpAppCompatFragment(R.layout.fragment_filter_materials), UserMvpView {
    private val binding: FragmentFilterMaterialsBinding by viewBinding()
    private var materialAdapter: MaterialAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private var searchInitiated = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        loadMaterials()
    }


    private fun initList(){
        materialAdapter = MaterialAdapter()
        with(binding.materialsList){
            adapter = materialAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }




    }

    private fun initSearch(materials: List<Property>){
        val searchEditText = binding.searchEditText
        val searchFlow = kotlinx.coroutines.flow.callbackFlow<kotlin.String> {
            val searchListener =
                object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {

                    }

                    override fun onTextChanged(
                        newText: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {
                        sendBlocking(newText.toString())

                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }

                }
            searchEditText.addTextChangedListener(searchListener)
            awaitClose { searchEditText.removeTextChangedListener(searchListener) }
        }
        presenter.collectMaterialSearchFlow(searchFlow, materials)

    }

    private fun loadMaterials(){
        presenter.getMaterials()
    }

   /* private fun generateList(): List<MaterialLine>{
        return listOf(
            MaterialLine(
                0,
            "Все материалы",
                false),
            MaterialLine(
                0,
                "Акрил",
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
    }*/



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
        if(filter.chosenMaterials.size == materialAdapter.getCurrentMaterials().size){
            materialAdapter.updateList(filter.chosenMaterials)
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
            is List<*> -> {
                materialAdapter.updateList((if(!searchInitiated) listOf(MaterialLine(0,"Все материалы", false)) else listOf<MaterialLine>()) + (result as List<Property>).map { MaterialLine(it.id, it.name, false) } )
                loadFilter()
                if(!searchInitiated){
                    initSearch(result as List<Property>)
                    searchInitiated = true
                }
            }
        }

    }

    override fun successNewCode() {

    }


}