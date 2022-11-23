package com.project.morestore.presentation.fragments

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
import com.project.morestore.presentation.adapters.MaterialAdapter
import com.project.morestore.databinding.FragmentFilterMaterialsBinding
import com.project.morestore.data.models.Filter
import com.project.morestore.data.models.MaterialLine
import com.project.morestore.data.models.Property
import com.project.morestore.domain.presenters.FilterPresenter
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.domain.presenters.UserPresenter
import com.project.morestore.presentation.mvpviews.FilterView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FilterMaterialsFragment: MvpAppCompatFragment(R.layout.fragment_filter_materials), FilterView {
    private val binding: FragmentFilterMaterialsBinding by viewBinding()
    private var materialAdapter: MaterialAdapter by autoCleared()
    @Inject
    lateinit var filterPresenter: FilterPresenter
    private val presenter by moxyPresenter { filterPresenter }
    private var searchInitiated = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        loadMaterials()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }


    private fun initList(){
        materialAdapter = MaterialAdapter(requireContext(),true){}
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
                        trySendBlocking(newText.toString())

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

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Материалы"
        binding.toolbar.actionTextView.isVisible = false
        binding.toolbar.arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
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


    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {
       binding.loader.isVisible = true
    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        when (result){
            is Filter -> bindFilter(result)
            is List<*> -> {
                materialAdapter.updateList((if(!searchInitiated) listOf(MaterialLine(0,"Все материалы", false, idCategory = -1)) else listOf<MaterialLine>()) + (result as List<Property>).map { MaterialLine(it.id, it.name, false, idCategory = it.idCategory?.toInt()!!) } )
                loadFilter()
                if(!searchInitiated){
                    initSearch(result as List<Property>)
                    searchInitiated = true
                }
            }
        }

    }
}