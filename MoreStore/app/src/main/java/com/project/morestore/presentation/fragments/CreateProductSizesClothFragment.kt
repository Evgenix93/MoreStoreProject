package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentCreateProductSizesBinding
import com.project.morestore.presentation.dialogs.SaveProductDialog
import com.project.morestore.data.models.*
import com.project.morestore.domain.presenters.CreateProductPresenter
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductSizesClothFragment :
    MvpAppCompatFragment(R.layout.fragment_create_product_sizes), CreateProductMvpView {
    private val binding: FragmentCreateProductSizesBinding by viewBinding()
    @Inject
    lateinit var mainPresenter: CreateProductPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private var sizeAdapter: SizeLineAdapter by autoCleared()
    private val args: CreateProductSizesClothFragmentArgs by navArgs()
    private var sizeProperty: Property2? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolbar()
        loadChosenSize()
    }


    private fun initToolbar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }

    }


    private fun getSizes() {
       presenter.getSizes(args.forWho, args.idCategory)
    }

    private fun initSaveButton(isChecked: Boolean){
        if(isChecked) {
            binding.showProductsBtn.isEnabled = true
            binding.showProductsBtn.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
        }
        else{
            binding.showProductsBtn.isEnabled = false
            binding.showProductsBtn.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }

         binding.showProductsBtn.setOnClickListener{
             saveSize()
             findNavController().popBackStack()
         }
    }

    private fun saveSize(){
        val size = sizeAdapter.getChosenSizes().first { it.isSelected }
        val property = Property2(size.id.toLong(), size.idCategory.toLong())
        presenter.removeProperty(size.idCategory.toLong())
        presenter.updateCreateProductData(extProperty = property)
    }

    private fun initList() {
        sizeAdapter = SizeLineAdapter(false, true, context = requireContext()){initSaveButton(it)}
        with(binding.sizesList) {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

    }



    private fun convertPropertyListToSizeLineList(properties: List<Property>): List<SizeLine> {
        val sizes =
            (properties)
                .map {
                    val list = it.ico?.split(';').orEmpty()
                    SizeLine(
                        it.id.toInt(),
                        it.name,
                       if(args.forWho != 2) list[0].removePrefix("W").removeSurrounding("'") else "",
                        if(args.forWho != 2) list[1].removePrefix("IT/RU/FR").removeSurrounding("'") else "",
                        if(args.forWho != 2) list[2].removePrefix("US").removeSurrounding("'") else "",
                        if(args.forWho != 2) list[3].removePrefix("UK").removeSurrounding("'") else "",
                        false,
                        idCategory = it.idCategory?.toInt()!!
                    )
                }

        return sizes

    }

    private fun loadChosenSize(){
        presenter.loadCreateProductData()
    }


    override fun loaded(result: Any) {
        binding.loader.isVisible = false

        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }
        if (result is List<*>) {
            sizeAdapter.updateList(
            convertPropertyListToSizeLineList(result as List<Property>) + listOf(
                SizeLine(-1, "", "", "", "", "", false, -1)
            ), null
        )
            val sizeLineList = convertPropertyListToSizeLineList(result as List<Property>)
            if(sizeProperty != null)
            sizeLineList.forEach {size ->
                if(size.id.toLong() == sizeProperty!!.value) {
                    size.isSelected = true
                    return@forEach
                }
            }
            sizeAdapter.updateList(
                sizeLineList + listOf(
                    SizeLine(-1, "", "", "", "", "", false, -1)
                ), null
            )
            initSaveButton(sizeLineList.any{it.isSelected})
        }
        if(result is com.project.morestore.data.models.CreateProductData){
            sizeProperty = result.property?.find{ property ->
              property.propertyCategory in 1L..8L
            }
            getSizes()
            binding.toolbar.actionIcon.setOnClickListener {
                if(result.id == null)
                    SaveProductDialog { presenter.createDraftProduct() }.show(childFragmentManager, null)
                else
                    findNavController().popBackStack(findNavController().previousBackStackEntry!!.destination.id, true)
            }
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