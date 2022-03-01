package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductDescriptionBinding
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.singletones.CreateProductData
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductDescriptionFragment: MvpAppCompatFragment(R.layout.fragment_create_product_description), MainMvpView {
    private val binding: FragmentCreateProductDescriptionBinding by viewBinding()
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

        binding.descriptionEditText.addTextChangedListener (onTextChanged = {string, _, _, _ ->
            if(string.isNullOrEmpty()) {
                binding.saveButton.isEnabled = false
                binding.saveButton.backgroundTintList =
                    ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
            }else{
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

    override fun loaded(result: Any) {
        TODO("Not yet implemented")
    }

    override fun loading() {
        TODO("Not yet implemented")
    }

    override fun error(message: String) {
        TODO("Not yet implemented")
    }

    override fun showOnBoarding() {
        TODO("Not yet implemented")
    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {
        TODO("Not yet implemented")
    }

    override fun success() {
        TODO("Not yet implemented")
    }
}