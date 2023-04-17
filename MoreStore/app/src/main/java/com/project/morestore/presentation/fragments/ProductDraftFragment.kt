package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.data.models.FeedbackProduct
import com.project.morestore.data.models.Product
import com.project.morestore.databinding.FragmentProductDraftBinding
import com.project.morestore.domain.presenters.ProductDraftPresenter
import com.project.morestore.presentation.adapters.FeedbackProductsAdapter
import com.project.morestore.presentation.mvpviews.ProductDraftView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class ProductDraftFragment: MvpAppCompatFragment(R.layout.fragment_product_draft), ProductDraftView {
    private val binding: FragmentProductDraftBinding by viewBinding()
    @Inject
    lateinit var productDraftPresenter: ProductDraftPresenter
    private val presenter by moxyPresenter { productDraftPresenter }
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
                it.property?.toTypedArray() ?: emptyArray(),
                it.packageDimensions,
                it.address
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
            null,
            feedbackProduct.price.toFloatOrNull() ?: 0f,
            feedbackProduct.newPrice?.toFloatOrNull() ?: 0f,
            feedbackProduct.sale,
            0L,
            feedbackProduct.address,
            feedbackProduct.photos?.toList() ?: emptyList(),
            null,
            null,
            null,
            null,
            feedbackProduct.brand,
            feedbackProduct.property?.toList(),
            null,
            null,
            null,
            feedbackProduct.packageDimensions,
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

}