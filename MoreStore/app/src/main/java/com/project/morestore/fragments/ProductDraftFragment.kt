package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentProductDraftBinding
import com.project.morestore.models.Product
import com.project.morestore.models.SuggestionModels
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ProductDraftFragment: MvpAppCompatFragment(R.layout.fragment_product_draft), MainMvpView {
    private val binding: FragmentProductDraftBinding by viewBinding()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }
    private var productAdapter: ProductAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        getDraftProducts()
    }


    private fun getDraftProducts(){
        presenter.getUserProductsWithStatus(5)

    }

    private fun initList(){
        productAdapter = ProductAdapter(null){ product ->
            findNavController().navigate(ProductDraftFragmentDirections.actionProductDraftFragmentToCreateProductStep6Fragment(product = product))

        }
        with(binding.list){
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    override fun loaded(result: Any) {
        productAdapter.updateList(result as List<Product>)


    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>, objectList: List<SuggestionModels>) {

    }

    override fun success() {

    }
}