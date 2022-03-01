package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductPriceBinding
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.singletones.CreateProductData
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductPriceFragment: MvpAppCompatFragment(R.layout.fragment_create_product_price), MainMvpView {
    private val binding: FragmentCreateProductPriceBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSaveButton()
    }



    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }

    }

    private fun initSaveButton(){
        binding.saveButton.setOnClickListener{
            savePrice()
            findNavController().popBackStack()
        }
    }

    private fun savePrice(){
      val originalPrice = binding.originalPriceEditText.text.toString().toInt()
      val salePrice = binding.salePriceEditText.text.toString().toInt()
        val discount = (((originalPrice.toFloat() - salePrice.toFloat()) / originalPrice.toFloat()) * 100).toInt()
      presenter.updateCreateProductData(price = originalPrice.toString(), sale = discount)
    }

    override fun loaded(result: Any) {

    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun success() {

    }

}