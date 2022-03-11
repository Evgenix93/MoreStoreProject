package com.project.morestore.fragments

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
import com.project.morestore.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentCreateProductSizesShoosBinding
import com.project.morestore.dialogs.SaveProductDialog
import com.project.morestore.models.*
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductSizesShoosFragment :
    MvpAppCompatFragment(R.layout.fragment_create_product_sizes_shoos), MainMvpView {
    private val binding: FragmentCreateProductSizesShoosBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var sizeAdapter: SizeLineAdapter by autoCleared()
    private val args: CreateProductSizesShoosFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        getSizes()
        initToolBar()
    }

    private fun initToolBar() {
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
        binding.toolbar.actionIcon.setOnClickListener { SaveProductDialog {presenter.createDraftProduct()}.show(childFragmentManager, null) }
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


    override fun loaded(result: Any) {

        if(result is CreatedProductId){
            findNavController().navigate(R.id.mainFragment)
            return
        }
        val otherSize = SizeLine(-1, "", "", "", "", "", false, -1)
        sizeAdapter.updateList(
            convertPropertyListToSizeLineList(result as List<Property>) + listOf(
                otherSize
            ), null
        )

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