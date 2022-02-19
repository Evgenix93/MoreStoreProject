package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentCreateProductSizesBinding
import com.project.morestore.models.Property
import com.project.morestore.models.SizeLine
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class CreateProductSizesClothFragment :
    MvpAppCompatFragment(R.layout.fragment_create_product_sizes), MainMvpView {
    private val binding: FragmentCreateProductSizesBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var sizeAdapter: SizeLineAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        getSizes()
    }


    private fun getSizes() {
        presenter.getTopSizesMen()

    }


    private fun initList() {
        sizeAdapter = SizeLineAdapter(false, true)
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
                        list[0].removePrefix("W").removeSurrounding("'"),
                        list[1].removePrefix("IT/RU/FR").removeSurrounding("'"),
                        list[2].removePrefix("US").removeSurrounding("'"),
                        list[3].removePrefix("UK").removeSurrounding("'"),
                        false,
                        idCategory = it.idCategory?.toInt()!!
                    )
                }

        return sizes

    }


    override fun loaded(result: Any) {
        sizeAdapter.updateList(
            convertPropertyListToSizeLineList(result as List<Property>) + listOf(
                SizeLine(-1, "", "", "", "", "", false, -1)
            ), null
        )
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