package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.presentation.adapters.ProductAdapter
import com.project.morestore.data.models.Product
import com.project.morestore.presentation.mvpviews.FavoritesMvpView
import com.project.morestore.domain.presenters.FavoritesPresenter
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesGoodsFragment : ListFragment(), FavoritesMvpView {
    @Inject
    lateinit var favoritesPresenter: FavoritesPresenter
    private val presenter by moxyPresenter { favoritesPresenter }
    private var productAdapter: ProductAdapter = ProductAdapter(null) { product ->
        findNavController().navigate(
            FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailsFragment(
                product = product,
                productId = null,
                isSeller = false
            )
        )
    }

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
        RecyclerView(requireContext())
            .apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = productAdapter

            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.getProductWishList()
    }


    override fun loading() {
      loader.isVisible = true
    }

    override fun loaded(result: Any) {
        loader.isVisible = false
        val list = result as List<*>
        showList()
        productAdapter.updateList(list as List<Product>)
    }

    override fun error(message: String) {
        loader.isVisible = false
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun emptyList() {
        showEmptyList {findNavController().navigate(R.id.catalogFragment) }
    }

}