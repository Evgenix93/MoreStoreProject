package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ProductAdapter
import com.project.morestore.presentation.adapters.SellerProfileAdapter
import com.project.morestore.databinding.PageProductsBinding
import com.project.morestore.data.models.Product
import com.project.morestore.util.autoCleared


class SellerProductsFragment : Fragment(R.layout.page_products) {
    private val binding: PageProductsBinding by viewBinding()
    private var productsAdapter: ProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        productsAdapter = ProductAdapter(null) { product ->
            findNavController().navigate(
                SellerProfileFragmentDirections.actionSellerProfileFragmentToProductDetailsFragment(
                    product = product,
                    isSeller = false,
                    productId = null
                )
            )
        }
        binding.productsRecyclerView.adapter = productsAdapter
        binding.productsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val products = arguments?.getParcelableArray(SellerProfileAdapter.PRODUCTS)
        if (products != null)
            productsAdapter.updateList(products.toList() as List<Product>)
    }
}