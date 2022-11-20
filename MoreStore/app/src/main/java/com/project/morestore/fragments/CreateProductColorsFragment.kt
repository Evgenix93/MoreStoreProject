package com.project.morestore.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ColorsAdapter
import com.project.morestore.databinding.FragmentCreateProductColorsBinding
import com.project.morestore.dialogs.SaveProductDialog
import com.project.morestore.data.models.*
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductColorsFragment: MvpAppCompatFragment(R.layout.fragment_create_product_colors), MainMvpView {
    private val binding:FragmentCreateProductColorsBinding by viewBinding()
    private var colorsAdapter: ColorsAdapter by autoCleared()
    @Inject lateinit var mainPresenter: MainPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private var colorProperties: List<Property2>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initColorsRecyclerView()
        initToolbar()
        loadChosenColors()
    }


    private fun initColorsRecyclerView(){
        colorsAdapter = ColorsAdapter(requireContext(), false){initSaveButton(it)}
        binding.colorsRecyclerView.adapter = colorsAdapter
        binding.colorsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun getColors(){
        presenter.getColors()
    }

    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener {
            findNavController().popBackStack()
        }


    }

    private fun initSaveButton(isChecked: Boolean){
        binding.saveButton.setOnClickListener{
            saveColors()
            findNavController().popBackStack()
        }
        if(isChecked){
            binding.saveButton.isEnabled = true
            binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
        }else{
            binding.saveButton.isEnabled = false
            binding.saveButton.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }
    }

    private fun saveColors(){
        val colors = colorsAdapter.getColors().filter{it.isChecked == true}
        val properties = colors.map{
            Property2(it.id, it.idCategory!!)
        }
        presenter.removeProperty(12)
        presenter.updateCreateProductData(extProperties = properties)
    }

    private fun loadChosenColors(){
        presenter.loadCreateProductData()
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun success() {

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        when(result){
            is CreatedProductId -> findNavController().navigate(R.id.mainFragment)
            is List<*> -> {

                val properties = result as List<Property>
                 colorProperties?.let{
                     it.forEach {colorProperty ->
                         properties.find{property ->
                             property.id == colorProperty.value
                         }?.isChecked = true
                     }
                 }
                colorsAdapter.updateColors(properties)
                initSaveButton(properties.any{it.isChecked == true})
            }
            is com.project.morestore.data.models.CreateProductData -> {
                colorProperties = result.property?.filter{ property ->
                    listOf(12L).any {
                        it == property.propertyCategory
                    }
                }
                getColors()
                binding.toolbar.actionIcon.setOnClickListener {
                    if(result.id == null)
                    SaveProductDialog { presenter.createDraftProduct() }.show(childFragmentManager, null)
                    else
                    findNavController().popBackStack(findNavController().previousBackStackEntry!!.destination.id, true)
                }
            }
        }
    }

}