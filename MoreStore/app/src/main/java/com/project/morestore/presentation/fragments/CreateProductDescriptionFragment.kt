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
import com.project.morestore.databinding.FragmentCreateProductDescriptionBinding
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
class CreateProductDescriptionFragment: MvpAppCompatFragment(R.layout.fragment_create_product_description), CreateProductMvpView {
    private val binding: FragmentCreateProductDescriptionBinding by viewBinding()
    @Inject lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSaveButton()
        loadDescription()
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initSaveButton(){

        binding.descriptionEditText.addTextChangedListener (onTextChanged = {string, _, _, _ ->
            if(string.isNullOrEmpty()) {
                binding.symbolsTextView.text = "0/3000"
                binding.saveButton.isEnabled = false
                binding.saveButton.backgroundTintList =
                    ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
            }else{
                binding.symbolsTextView.text = "${string.length}/3000"
                binding.saveButton.isEnabled = true
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
            }
        })

        binding.saveButton.setOnClickListener{
            saveDescription()
            findNavController().popBackStack()
        }
    }

    private fun saveDescription(){
        presenter.updateCreateProductData(about = binding.descriptionEditText.text.toString())
    }

    private fun loadDescription(){
       presenter.loadCreateProductData()
    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false

        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }

        val createProductData = result as com.project.morestore.data.models.CreateProductData
        binding.descriptionEditText.setText(createProductData.about)
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