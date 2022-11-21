package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ProductAdapter
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.ProductBrand
import com.project.morestore.presentation.mvpviews.FavoritesMvpView
import com.project.morestore.domain.presenters.FavoritesPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesBrandsFragment :ListFragment(), FavoritesMvpView {
    private lateinit var productAdapter: ProductAdapter
    @Inject lateinit var favoritesPresenter: FavoritesPresenter
    private val presenter by moxyPresenter{ favoritesPresenter }

    private fun getFavoriteBrands(){
        loader.isVisible = true
        presenter.getBrandWishList()
    }

    private fun getProducts(brands: List<ProductBrand>){
        presenter.getProducts(brands)
    }

    override val emptyList by lazy {
        EmptyList(
            R.drawable.img_favoritesbrands_empty1,
            R.drawable.img_favoritesbrands_empty2,
            R.drawable.img_favoritesbrands_empty3,
            requireContext().getString(R.string.favorite_brandsListEmpty_message),
            requireContext().getString(R.string.favorite_listEmpty_actionText)
        )
    }
    override val list by lazy {
        RecyclerView(requireContext())
            .apply {
                adapter = ProductAdapter(null){findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailsFragment(it, null, false))}.also { productAdapter = it }
                layoutManager = GridLayoutManager(requireContext(), 2)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFavoriteBrands()
    }



    override fun loading() {

    }

    override fun loaded(result: Any) {
        val list = result as List<*>
           if(list.isNotEmpty()) {
               showList()
               when (list[0]) {
                   is ProductBrand -> {
                       getProducts(list as List<ProductBrand>)
                       showBrandsView(list as List<ProductBrand>)
                   }
                   is Product -> {
                       loader.isVisible = false
                       productAdapter.updateList(list as List<Product>)
                   }
               }
           }else {
               loader.isVisible = false
               showEmptyList { }
           }
    }

    override fun error(message: String) {
        loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun emptyList() {
        loader.isVisible = false
        showEmptyList { findNavController().navigate(R.id.catalogFragment) }
    }

    override fun success() {

    }
}