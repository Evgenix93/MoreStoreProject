package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductPackageBinding
import com.project.morestore.presentation.dialogs.SaveProductDialog
import com.project.morestore.data.models.CreateProductData
import com.project.morestore.data.models.CreatedProductId
import com.project.morestore.data.models.ProductDimensions
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductPackageFragment: MvpAppCompatFragment(R.layout.fragment_create_product_package), CreateProductMvpView {
    private val binding: FragmentCreateProductPackageBinding by viewBinding()
    @Inject lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEditTexts()
        initToolbar()
        presenter.loadCreateProductData()
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }
    }


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
            length = binding.lengthEditText.text.toString(),
            width = binding.widthEditText.text.toString(),
            height = binding.heightEditText.text.toString(),
            weight = binding.weightEditText.text.toString()
        ))
    }

    private fun initEditTexts(){
        binding.heightEditText.addTextChangedListener {
            initSaveButton(binding.heightEditText.text?.isNotBlank() == true
                    && binding.widthEditText.text?.isNotBlank() == true
                    && binding.lengthEditText.text?.isNotBlank() == true
                    && binding.weightEditText.text?.isNotBlank() == true)
        }

        binding.widthEditText.addTextChangedListener {
            initSaveButton(binding.heightEditText.text?.isNotBlank() == true
                    && binding.widthEditText.text?.isNotBlank() == true
                    && binding.lengthEditText.text?.isNotBlank() == true
                    && binding.weightEditText.text?.isNotBlank() == true)
        }

        binding.lengthEditText.addTextChangedListener {
            initSaveButton(binding.heightEditText.text?.isNotBlank() == true
                    && binding.widthEditText.text?.isNotBlank() == true
                    && binding.lengthEditText.text?.isNotBlank() == true
                    && binding.weightEditText.text?.isNotBlank() == true)
        }

        binding.weightEditText.addTextChangedListener {
            initSaveButton(binding.heightEditText.text?.isNotBlank() == true
                    && binding.widthEditText.text?.isNotBlank() == true
                    && binding.lengthEditText.text?.isNotBlank() == true
                    && binding.weightEditText.text?.isNotBlank() == true)
        }
    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }
        val productData = result as CreateProductData
        if(productData.packageDimensions != null){
            binding.heightEditText.setText(productData.packageDimensions!!.height)
            binding.widthEditText.setText(productData.packageDimensions!!.width)
            binding.lengthEditText.setText(productData.packageDimensions!!.length)
            binding.weightEditText.setText(productData.packageDimensions!!.weight)
            initSaveButton(true)
        }
        binding.toolbar.actionIcon.setOnClickListener {
            if(result.id == null)
                SaveProductDialog { presenter.createDraftProduct() }.show(childFragmentManager, null)
            else
                findNavController().popBackStack(findNavController().previousBackStackEntry!!.destination.id, true)
        }
    }

    override fun loading() {
        binding.loader.isVisible = true

    }

    override fun error(message: String) {
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }



}