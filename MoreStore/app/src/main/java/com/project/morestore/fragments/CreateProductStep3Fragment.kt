package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ShoosTypeCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep3Binding
import com.project.morestore.util.autoCleared

class CreateProductStep3Fragment: Fragment(R.layout.fragment_create_product_step3) {
    private val binding: FragmentCreateProductStep3Binding by viewBinding()
    private var shoosTypeAdapter: ShoosTypeCreateProductAdapter by autoCleared()
    private val args: CreateProductStep3FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        setClickListeners()
        initToolbar()
    }


    private fun setClickListeners(){
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep3FragmentDirections.actionCreateProductStep3FragmentToCreateProductHowToSellFragment())
        }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 3 из 6"
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }


    private fun initList(){
        shoosTypeAdapter = ShoosTypeCreateProductAdapter {
            findNavController().navigate(CreateProductStep3FragmentDirections.actionCreateProductStep3FragmentToCreateProductStep5Fragment(args.category))
        }
        with(binding.itemsList){
            adapter = shoosTypeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        shoosTypeAdapter.notifyDataSetChanged()

    }
}