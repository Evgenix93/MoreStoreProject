package com.project.morestore.presentation.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentCreateProductSizesShoosBinding
import com.project.morestore.presentation.dialogs.SaveProductDialog
import com.project.morestore.data.models.*
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class CreateProductSizesShoosFragment :
    MvpAppCompatFragment(R.layout.fragment_create_product_sizes_shoos), MainMvpView {
    private val binding: FragmentCreateProductSizesShoosBinding by viewBinding()
    @Inject
    lateinit var mainPresenter: MainPresenter
    private val presenter by moxyPresenter { mainPresenter }
    private var sizeAdapter: SizeLineAdapter by autoCleared()
    private val args: CreateProductSizesShoosFragmentArgs by navArgs()
    private var sizeProperty: Property2? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        loadChosenSize()
    }

    private fun initToolBar() {
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }

    }

    private fun initList() {
        sizeAdapter = SizeLineAdapter(true, true, context = requireContext()) { initSaveButton(it) }
        with(binding.sizesList) {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun getSizes() {
        presenter.getSizesShoos(args.forWho)
    }

    private fun initSaveButton(isChecked: Boolean) {
        if (isChecked) {
            binding.showProductsBtn.isEnabled = true
            binding.showProductsBtn.backgroundTintList =
                ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.black, null))
        } else {
            binding.showProductsBtn.isEnabled = false
            binding.showProductsBtn.backgroundTintList =
                ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray1, null))
        }

        binding.showProductsBtn.setOnClickListener {
            saveSize()
            findNavController().popBackStack()
        }
    }

    private fun saveSize() {
        val size = sizeAdapter.getChosenSizes().first { it.isSelected }
        val property = Property2(size.id.toLong(), size.idCategory.toLong())
        presenter.removeProperty(size.idCategory.toLong())
        presenter.updateCreateProductData(extProperty = property)
    }

    private fun convertPropertyListToSizeLineList(properties: List<Property>): List<SizeLine> {
        val listShoosSizes =
            (properties)
                .map {
                    val list = it.ico?.split(';').orEmpty()
                    SizeLine(
                        it.id.toInt(),
                        it.name,
                        "",
                        if(args.forWho != 2) list[0].removePrefix("FR").removeSurrounding("'") else "",
                        if(args.forWho != 2) list[1].removePrefix("US").removeSurrounding("'") else "",
                        if(args.forWho != 2) list[2].removePrefix("UK").removeSurrounding("'") else "",
                        false,
                        idCategory = it.idCategory?.toInt()!!
                    )

                }

        return listShoosSizes
    }

    private fun loadChosenSize(){
        presenter.loadCreateProductData()
    }

    override fun loaded(result: Any) {

        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }
        if(result is List<*>) {
            val otherSize = SizeLine(-1, "", "", "", "", "", false, -1)
            sizeAdapter.updateList(
            convertPropertyListToSizeLineList(result as List<Property>) + listOf(
                otherSize
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
                    otherSize
                ), null
            )
            initSaveButton(sizeLineList.any{it.isSelected})
        }
        if(result is com.project.morestore.data.models.CreateProductData){
            sizeProperty = result.property?.find{property ->
                listOf(3L, 6L, 9L).any {
                    it == property.propertyCategory
                }
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

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun success() {

    }
}