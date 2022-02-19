package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.OptionsAdapter
import com.project.morestore.databinding.FragmentAddProductDetailsBinding
import com.project.morestore.util.autoCleared

class CreateProductFragment: Fragment(R.layout.fragment_add_product_details) {
    private val binding: FragmentAddProductDetailsBinding by viewBinding()
    private var optionsAdapter: OptionsAdapter by autoCleared()
    private val args: CreateProductFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOptionsRecyclerView()
        setClickListeners()
        initToolbar()
    }

    private fun initOptionsRecyclerView(){
        optionsAdapter = OptionsAdapter(requireContext()){ position ->
            when(position){
                0 -> findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductAddPhotoFragment())
                1 -> findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductConditionFragment())
                2 -> findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductPriceFragment())
                3 -> findNavController().navigate(if(args.category.name == "Обувь") CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductSizesShoosFragment() else CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductSizesClothFragment())
                4 -> findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductDescriptionFragment())
                5 -> findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductColorsFragment())
                6 -> findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductMaterialsFragment())
                7 -> findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductRegionsFragment())

            }

        }
        binding.listRecyclerView.adapter = optionsAdapter
        binding.listRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setClickListeners(){
        binding.addCardButton.setOnClickListener {
            findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToAddCardFragment())
        }
        binding.howToSellTextView.setOnClickListener {
            findNavController().navigate(CreateProductFragmentDirections.actionCreateProductFragmentToCreateProductHowToSellFragment())
        }
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Шаг 6 из 6"
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }
}