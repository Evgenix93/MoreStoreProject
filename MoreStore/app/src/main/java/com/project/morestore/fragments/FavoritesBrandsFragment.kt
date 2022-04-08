package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.models.Product
import com.project.morestore.models.ProductBrand
import com.project.morestore.mvpviews.FavoritesMvpView
import com.project.morestore.presenters.FavoritesPresenter
import com.project.morestore.util.autoCleared
import moxy.ktx.moxyPresenter

class FavoritesBrandsFragment :ListFragment(), FavoritesMvpView {
    private var productAdapter: ProductAdapter by autoCleared()
    private val presenter by moxyPresenter{ FavoritesPresenter(requireContext()) }

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
       // showEmptyList { Toast.makeText(requireContext(), "Список пуст", Toast.LENGTH_SHORT).show() }
        getFavoriteBrands()
    }

    override fun favoritesLoaded(list: List<*>) {
           if(list.isNotEmpty()) {
               showList()
               when (list[0]) {
                   is ProductBrand -> {
                       getProducts(list as List<ProductBrand>)
                       showBrandsView(list as List<ProductBrand>)
                   }
                   is Product -> {
                       loader.isVisible = false
                       Log.d("MyDebug", "products loaded")
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
}