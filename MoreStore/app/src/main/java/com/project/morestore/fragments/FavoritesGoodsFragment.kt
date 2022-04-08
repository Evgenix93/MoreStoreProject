package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.adapters.ProductAdapter
import com.project.morestore.models.Product
import com.project.morestore.mvpviews.FavoritesMvpView
import com.project.morestore.presenters.FavoritesPresenter
import com.project.morestore.presenters.MainPresenter
import com.project.morestore.util.autoCleared
import moxy.ktx.moxyPresenter
import kotlin.reflect.KClass

class FavoritesGoodsFragment : ListFragment(), FavoritesMvpView {
    private val presenter by moxyPresenter { FavoritesPresenter(requireContext()) }
    private var productAdapter: ProductAdapter by autoCleared()
    override val emptyList by lazy {
        EmptyList(
            R.drawable.img_favorites_empty1,
            R.drawable.img_favorites_empty2,
            R.drawable.img_favorites_empty3,
            requireContext().getString(R.string.favorite_goodsListEmpty_message),
            requireContext().getString(R.string.favorite_listEmpty_actionText)
        )
    }
    override val list by lazy {
        productAdapter = ProductAdapter(null) { product ->
            findNavController().navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailsFragment(
                    product = product,
                    productId = null,
                    isSeller = false
                )
            )

        }
        RecyclerView(requireContext())
            .apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = productAdapter
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //showEmptyList { Toast.makeText(requireContext(), "123", Toast.LENGTH_SHORT).show() }
        presenter.getProductWishList()
    }

    override fun loading() {

    }

    override fun favoritesLoaded(list: List<*>) {
        productAdapter.updateList(list as List<Product>)
        showList()

    }

    override fun error(message: String) {

    }

    override fun emptyList() {
        showEmptyList { }
    }

    override fun success() {

    }
}