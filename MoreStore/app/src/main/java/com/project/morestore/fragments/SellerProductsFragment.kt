package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.adapters.SellerProfileAdapter
import com.project.morestore.databinding.PageProductsBinding
import com.project.morestore.models.Product
import com.project.morestore.models.User
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class SellerProductsFragment: Fragment(R.layout.page_products) {
   // private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private val binding: PageProductsBinding by viewBinding()
    private var productsAdapter: ProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

    }

    private fun initRecyclerView(){
        productsAdapter = ProductAdapter(null){}
        binding.productsRecyclerView.adapter = productsAdapter
        binding.productsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val products = arguments?.getParcelableArray(SellerProfileAdapter.PRODUCTS)
        Log.d("Debug", "products = ${products?.toList()}")
        if(products != null)
            productsAdapter.updateList(products.toList() as List<Product>)
    }



}