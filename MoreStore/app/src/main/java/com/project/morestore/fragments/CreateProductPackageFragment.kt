package com.project.morestore.fragments

import android.content.res.ColorStateList
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.models.ProductDimensions
import com.project.morestore.presenters.MainPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductPackageFragment: MvpAppCompatFragment(R.layout.fragment_product_package) {
    private val binding: FragmentProductPackageBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }


    private fun initSaveButton(isChecked: Boolean){

        if(isChecked){
            binding.saveButton.isEnabled = true
            binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
        }else{
            binding.saveButton.isEnabled = false
            binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }

        binding.saveButton.setOnClickListener{
            saveDimensions()
            findNavController().popBackStack()
        }
    }

    private fun saveDimensions(){
        presenter.updateCreateProductData(dimensions = ProductDimensions(
            length = "",
            width = "",
            height = "",
            weight = ""
        ))
    }



}