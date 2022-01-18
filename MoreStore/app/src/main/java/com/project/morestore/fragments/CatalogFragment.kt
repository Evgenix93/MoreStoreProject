package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentCatalogBinding
import com.project.morestore.util.autoCleared

class CatalogFragment: Fragment(R.layout.fragment_catalog) {
    private val binding: FragmentCatalogBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
    }



    private fun initList(){
        productAdapter = ProductAdapter(10)
        with(binding.productList){
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
            productAdapter.notifyDataSetChanged()
        }
    }
}