package com.project.morestore.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.MaterialAdapter
import com.project.morestore.databinding.FragmentCreateProductMaterialsBinding
import com.project.morestore.databinding.FragmentFilterMaterialsBinding
import com.project.morestore.models.Filter
import com.project.morestore.models.MaterialLine
import com.project.morestore.models.Property
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductMaterialsFragment: MvpAppCompatFragment(R.layout.fragment_create_product_materials), MainMvpView {
    private val binding: FragmentCreateProductMaterialsBinding by viewBinding()
    private var materialAdapter: MaterialAdapter by autoCleared()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var searchInitiated = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        loadMaterials()
        initToolbar()
    }



    private fun initList(){
        materialAdapter = MaterialAdapter(requireContext(),false)
        with(binding.materialsRecyclerView){
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




    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }




    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun showOnBoarding() {
        TODO("Not yet implemented")
    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {
        TODO("Not yet implemented")
    }

    override fun success() {
        TODO("Not yet implemented")
    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        when (result){

            is List<*> -> {
                materialAdapter.updateList((result as List<Property>).map { MaterialLine(it.id, it.name, false, idCategory = it.idCategory?.toInt()!!) } )

                if(!searchInitiated){
                    initSearch(result as List<Property>)
                    searchInitiated = true
                }
            }
        }

    }

}