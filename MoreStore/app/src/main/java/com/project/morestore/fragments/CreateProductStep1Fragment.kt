package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ForWhoCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep1Binding
import com.project.morestore.util.autoCleared

class CreateProductStep1Fragment: Fragment(R.layout.fragment_create_product_step1) {
    private val binding: FragmentCreateProductStep1Binding by viewBinding()
    private var forWhoAdapter: ForWhoCreateProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        setClickListeners()
        initToolbar()

    }


    private fun setClickListeners(){
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep1FragmentDirections.actionCreateProductStep1FragmentToCreateProductHowToSellFragment())
        }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 1 из 6"

        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }
    }

    private fun initList(){
        forWhoAdapter = ForWhoCreateProductAdapter{ position ->
            findNavController().navigate(CreateProductStep1FragmentDirections.actionCreateProductStep1FragmentToCreateProductStep2Fragment(position))

        }
        with(binding.itemsList){
            adapter = forWhoAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        forWhoAdapter.notifyDataSetChanged()

    }

   fun saveProduct(productId: Long){

   }
}