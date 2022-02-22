package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.CloathStyleCreateProductAdapter
import com.project.morestore.databinding.FragmentCreateProductStep3Binding
import com.project.morestore.databinding.FragmentCreateProductStep4Binding
import com.project.morestore.util.autoCleared

class CreateProductStep4Fragment: Fragment(R.layout.fragment_create_product_step4) {
    private val binding: FragmentCreateProductStep4Binding by viewBinding()
    private var styleAdapter: CloathStyleCreateProductAdapter by autoCleared()
    private val args: CreateProductStep4FragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        setClickListeners()
        initToolbar()
    }


    private fun setClickListeners(){
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductStep4FragmentDirections.actionCreateProductStep4FragmentToCreateProductHowToSellFragment())
        }

        binding.skipBtn.setOnClickListener { findNavController().navigate(R.id.skipStepDialog) }
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.titleTextView.text = "Шаг 4 из 6"
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }


    private fun initList(){
        styleAdapter = CloathStyleCreateProductAdapter{
            findNavController().navigate(CreateProductStep4FragmentDirections.actionCreateProductStep4FragmentToCreateProductStep5Fragment(args.category))
        }
        with(binding.itemsList){
            adapter = styleAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        styleAdapter.notifyDataSetChanged()


    }


}