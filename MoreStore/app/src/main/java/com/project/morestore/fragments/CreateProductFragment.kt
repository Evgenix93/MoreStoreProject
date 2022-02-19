package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.OptionsAdapter
import com.project.morestore.databinding.FragmentAddProductDetailsBinding
import com.project.morestore.util.autoCleared

class CreateProductFragment: Fragment(R.layout.fragment_add_product_details) {
    private val binding: FragmentAddProductDetailsBinding by viewBinding()
    private var optionsAdapter: OptionsAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOptionsRecyclerView()
    }

    private fun initOptionsRecyclerView(){
        optionsAdapter = OptionsAdapter(requireContext())
        binding.listRecyclerView.adapter = optionsAdapter
        binding.listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}