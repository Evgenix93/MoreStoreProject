package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductDescriptionBinding
import com.project.morestore.dialogs.SaveProductDialog
import com.project.morestore.models.CreatedProductId
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductDescriptionFragment: MvpAppCompatFragment(R.layout.fragment_create_product_description), MainMvpView {
    private val binding: FragmentCreateProductDescriptionBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initSaveButton()
        loadDescription()
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
       // binding.toolbar.actionIcon.setOnClickListener { SaveProductDialog {presenter.createDraftProduct()}.show(childFragmentManager, null) }

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

        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }

        val createProductData = result as com.project.morestore.models.CreateProductData
        binding.descriptionEditText.setText(createProductData.about)
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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