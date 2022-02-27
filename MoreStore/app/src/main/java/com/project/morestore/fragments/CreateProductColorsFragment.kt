package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ColorsAdapter
import com.project.morestore.databinding.FragmentColorsBinding
import com.project.morestore.databinding.FragmentCreateProductColorsBinding
import com.project.morestore.models.Filter
import com.project.morestore.models.Property
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductColorsFragment: MvpAppCompatFragment(R.layout.fragment_create_product_colors), MainMvpView {
    private val binding:FragmentCreateProductColorsBinding by viewBinding()
    private var colorsAdapter: ColorsAdapter by autoCleared()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initColorsRecyclerView()
        getColors()
        initToolbar()
    }


    private fun initColorsRecyclerView(){
        colorsAdapter = ColorsAdapter(requireContext())
        binding.colorsRecyclerView.adapter = colorsAdapter
        binding.colorsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getColors(){
        presenter.getColors()
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }


    override fun error(message: String) {

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
        when(result){
            is List<*> -> {
                val properties = result as List<Property>
                colorsAdapter.updateColors(properties)
            }
        }
    }

}