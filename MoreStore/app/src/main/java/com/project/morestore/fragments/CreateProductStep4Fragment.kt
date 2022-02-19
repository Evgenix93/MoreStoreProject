package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.CloathStyleCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep3Binding
import com.project.morestore.util.autoCleared

class CreateProductStep4Fragment: Fragment(R.layout.fragment_create_product_step4) {
    private val binding: FragmentCreateProductStep3Binding by viewBinding()
    private var styleAdapter: CloathStyleCreateProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        setClickListeners()
        initToolbar()
    }


    private fun setClickListeners(){
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep2FragmentDirections.actionCreateProductStep2FragmentToCreateProductHowToSellFragment())
        }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 4 из 6"
    }


    private fun initList(){
        styleAdapter = CloathStyleCreateProductAdapter()
        with(binding.itemsList){
            adapter = styleAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        styleAdapter.notifyDataSetChanged()


    }


}