package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.project.morestore.R
import com.project.morestore.adapters.PhotoViewPagerAdapter
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentProductBinding
import com.project.morestore.util.autoCleared

class ProductDetailsFragment: Fragment(R.layout.fragment_product) {
    private val binding: FragmentProductBinding by viewBinding()
    private var productAdapter: ProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initList()
        initToolBar()
    }

    private fun initViewPager(){
        val photoAdapter = PhotoViewPagerAdapter(this)
        photoAdapter.updateList(listOf("1", "2", "3"))
        binding.productPhotoViewPager.adapter = photoAdapter
        binding.viewPagerDots.setViewPager2(binding.productPhotoViewPager)

        //TabLayoutMediator(binding.viewPagerDots, binding.productPhotoViewPager) { tab, position ->
            ////Some implementation
        //}.attach()


    }

    private fun initToolBar(){
        binding.toolbar.titleTextView.text = "Вечернее платье"
        binding.toolbar.actionIcon.setImageResource(R.drawable.ic_cart)
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initList(){
        productAdapter = ProductAdapter(4){}
        with(binding.productList){
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }

    }
}