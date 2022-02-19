package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentCreateProductSizesShoosBinding
import com.project.morestore.models.Property
import com.project.morestore.models.SizeLine
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductSizesShoosFragment: MvpAppCompatFragment(R.layout.fragment_create_product_sizes_shoos), MainMvpView {
    private val binding: FragmentCreateProductSizesShoosBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var sizeAdapter: SizeLineAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        getSizes()
        initToolBar()
    }

    private fun initToolBar(){
        binding.toolbar.backIcon.setOnClickListener { findNavController().popBackStack() }
        binding.toolbar.actionIcon.setOnClickListener { findNavController().navigate(R.id.saveProductDialog) }
    }

    private fun initList(){
        sizeAdapter = SizeLineAdapter(true, true, context = requireContext())
        with(binding.sizesList){
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun getSizes(){
        presenter.getShoosMen()
    }

    private fun convertPropertyListToSizeLineList(properties: List<Property>): List<SizeLine>{
        val listShoosSizes =
            (properties)
                .map {
                    val list = it.ico?.split(';').orEmpty()
                    SizeLine(it.id.toInt(), it.name, "", list[0].removePrefix("FR").removeSurrounding("'"), list[1].removePrefix("US").removeSurrounding("'"), list[2].removePrefix("UK").removeSurrounding("'"), false, idCategory = it.idCategory?.toInt()!!)

                }

        return listShoosSizes
    }










    override fun loaded(result: Any) {
        val otherSize = SizeLine(-1, "", "", "", "", "", false, -1)
        sizeAdapter.updateList(convertPropertyListToSizeLineList(result as List<Property>) + listOf(otherSize), null)

    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>) {

    }

    override fun success() {

    }
}