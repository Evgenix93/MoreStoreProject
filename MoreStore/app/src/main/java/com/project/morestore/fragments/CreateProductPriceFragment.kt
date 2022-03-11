package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductPriceBinding
import com.project.morestore.dialogs.SaveProductDialog
import com.project.morestore.models.CreatedProductId
import com.project.morestore.models.Product
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
        loadPrice()
    }



    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
        binding.toolbar.actionIcon.setOnClickListener { SaveProductDialog {presenter.createDraftProduct()}.show(childFragmentManager, null) }

    }

    private fun initSaveButton(){
        binding.originalPriceEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.isNullOrEmpty()){
                   // binding.saveButton.isEnabled = false
                    binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
                }else{
                    if(binding.salePriceEditText.text.toString().isNotEmpty()){
                     //   binding.saveButton.isEnabled = true
                        binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.salePriceEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.isNullOrEmpty()){
                    binding.saveButton.isEnabled = false
                    binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
                }else{
                    if(binding.originalPriceEditText.text.toString().isNotEmpty()){
                        binding.saveButton.isEnabled = true
                        binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.saveButton.setOnClickListener{
            savePrice()
            findNavController().popBackStack()
        }
    }


    private fun savePrice(){
      val originalPrice = binding.originalPriceEditText.text.toString().toInt()
      val salePrice = binding.salePriceEditText.text.toString().toInt()
        val discount = (((originalPrice.toFloat() - salePrice.toFloat()) / originalPrice.toFloat()) * 100)
      presenter.updateCreateProductData(price = originalPrice.toString(), sale = discount, newPrice = salePrice.toString())
    }

    private fun loadPrice(){
        presenter.loadCreateProductData()
    }

    override fun loaded(result: Any) {

        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }


      val createProductData = result as com.project.morestore.models.CreateProductData
      binding.originalPriceEditText.setText(createProductData.price)
      binding.salePriceEditText.setText(createProductData.priceNew)
    }

    override fun loading() {

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }




    override fun success() {

    }

}