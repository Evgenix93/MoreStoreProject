package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentCreateProductStylesBinding
import com.project.morestore.presentation.dialogs.SaveProductDialog
import com.project.morestore.data.models.CreateProductData
import com.project.morestore.data.models.CreatedProductId
import com.project.morestore.data.models.Property2
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductStylesFragment: MvpAppCompatFragment(R.layout.fragment_create_product_styles), CreateProductMvpView {
    private val binding: FragmentCreateProductStylesBinding by viewBinding()
    @Inject
    lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initChecking()
        initSaveButton()
        loadStyles()
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initSaveButton(){
        binding.saveButton.setOnClickListener{
            saveStyles()
            findNavController().popBackStack()
        }
    }

    private fun initChecking(){
        binding.eveningCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.busynessCheckBox.isChecked = false
                binding.usualCheckBox.isChecked = false
                binding.sportCheckBox.isChecked = false
            }
            binding.saveButton.isEnabled = isChecked
            if(binding.saveButton.isEnabled)
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
            else
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }
        binding.busynessCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.eveningCheckBox.isChecked = false
                binding.usualCheckBox.isChecked = false
                binding.sportCheckBox.isChecked = false
            }
            binding.saveButton.isEnabled = isChecked
            if(binding.saveButton.isEnabled)
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
            else
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }
        binding.usualCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                binding.busynessCheckBox.isChecked = false
                binding.eveningCheckBox.isChecked = false
                binding.sportCheckBox.isChecked = false
            }
            binding.saveButton.isEnabled = isChecked
            if(binding.saveButton.isEnabled)
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
            else
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }
        binding.sportCheckBox.setOnCheckedChangeListener { _, isChecked ->
              if(isChecked) {
                  binding.busynessCheckBox.isChecked = false
                  binding.eveningCheckBox.isChecked = false
                  binding.usualCheckBox.isChecked = false
              }
             binding.saveButton.isEnabled = isChecked
            if(binding.saveButton.isEnabled)
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
            else
                binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }
    }

    private fun saveStyles(){
        val property =  when {
            binding.eveningCheckBox.isChecked -> Property2(143, 10)
            binding.busynessCheckBox.isChecked -> Property2(108, 10)
            binding.usualCheckBox.isChecked -> Property2(109, 10)
            binding.sportCheckBox.isChecked -> Property2(110, 10)
            else -> Property2(143, 10)
        }
        presenter.removeProperty(10)
        presenter.updateCreateProductData(extProperty = property)
    }

    private fun loadStyles(){
        presenter.loadCreateProductData()
    }

    override fun loaded(result: Any) {
        binding.loader.isVisible = false
        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }

        val createProductData = result as CreateProductData
        binding.toolbar.actionIcon.setOnClickListener {
            if(result.id == null)
                SaveProductDialog { presenter.createDraftProduct() }.show(childFragmentManager, null)
            else
                findNavController().popBackStack(findNavController().previousBackStackEntry!!.destination.id, true)
        }
        val conditionProperty = createProductData.property?.find{it.propertyCategory == 10L} ?: return
        when (conditionProperty.value){
            143L -> binding.eveningCheckBox.isChecked = true
            108L -> binding.busynessCheckBox.isChecked = true
            109L -> binding.usualCheckBox.isChecked = true
            110L -> binding.sportCheckBox.isChecked = true
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