package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.FeedbackProductsAdapter
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.databinding.FragmentProductDraftBinding
import com.project.morestore.models.FeedbackProduct
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
    private var productAdapter: FeedbackProductsAdapter by autoCleared()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        getDraftProducts()
        initToolbar()
    }


    private fun getDraftProducts(){
        presenter.getUserProductsWithStatus(5)

    }

    private fun initList(){
      /*  productAdapter = ProductAdapter(null){ product ->
            findNavController().navigate(ProductDraftFragmentDirections.actionProductDraftFragmentToCreateProductStep6Fragment(product = product))

        }*/
        productAdapter = FeedbackProductsAdapter{onItemClick(it)}
        with(binding.list){
            adapter = productAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun initToolbar(){
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateList(list: List<Product>){
        val feedBackProducts = list.map{
            FeedbackProduct(
                it.id,
                it.photo.toTypedArray(),
                it.name,
                it.brand,
                it.price.toDouble().toString(),
                it.priceNew?.toDouble().toString(),
                it.sale,
                it.category,
                it.property?.toTypedArray() ?: emptyArray()
            )
        }
        productAdapter.setItems(feedBackProducts)
    }

    private fun onItemClick(feedbackProduct: FeedbackProduct){
        val product = Product(
            feedbackProduct.id,
            feedbackProduct.title,
            "",
            "",
            "",
            5,
            feedbackProduct.price.toFloatOrNull() ?: 0f,
            feedbackProduct.newPrice?.toFloatOrNull() ?: 0f,
            feedbackProduct.sale,
            0L,
            null,
            feedbackProduct.photos?.toList() ?: emptyList(),
            null,
            null,
            null,
            null,
            feedbackProduct.brand,
            feedbackProduct.property?.toList(),
            null
        )
        findNavController().navigate(ProductDraftFragmentDirections.actionProductDraftFragmentToCreateProductStep6Fragment(product = product))
    }

    override fun loaded(result: Any) {
        updateList(result as List<Product>)
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