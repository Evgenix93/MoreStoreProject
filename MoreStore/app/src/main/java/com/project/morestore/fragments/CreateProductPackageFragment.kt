package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductPackageBinding
import com.project.morestore.dialogs.SaveProductDialog
import com.project.morestore.models.CreateProductData
import com.project.morestore.models.CreatedProductId
import com.project.morestore.models.ProductDimensions
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductPackageFragment: MvpAppCompatFragment(R.layout.fragment_create_product_package), MainMvpView {
    private val binding: FragmentCreateProductPackageBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

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
       // binding.toolbar.actionIcon.setOnClickListener { SaveProductDialog {presenter.createDraftProduct()}.show(childFragmentManager, null) }

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

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun loginFailed() {

    }

    override fun success() {

    }


}