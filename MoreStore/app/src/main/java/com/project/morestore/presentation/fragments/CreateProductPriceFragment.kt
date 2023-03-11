package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductPriceBinding
import com.project.morestore.presentation.dialogs.SaveProductDialog
import com.project.morestore.data.models.CreatedProductId
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductPriceFragment: MvpAppCompatFragment(R.layout.fragment_create_product_price), CreateProductMvpView {
    private val binding: FragmentCreateProductPriceBinding by viewBinding()
    @Inject lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSaveButton()
        loadPrice()
    }


    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initSaveButton(){
        binding.originalPriceEditText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.isNullOrEmpty()){

                    binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
                }else{
                    if(binding.salePriceEditText.text.toString().isNotEmpty()){
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
        binding.loader.isVisible = false

        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }


      val createProductData = result as com.project.morestore.data.models.CreateProductData
      binding.originalPriceEditText.setText("${createProductData.price?.toFloatOrNull()?.toInt() ?: ""}")
      binding.salePriceEditText.setText("${createProductData.priceNew?.toFloatOrNull()?.toInt() ?: ""}")
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


}