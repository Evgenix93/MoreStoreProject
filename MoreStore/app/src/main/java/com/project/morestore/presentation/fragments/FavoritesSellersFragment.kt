package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.presentation.adapters.FavoriteSellersAdapter
import com.project.morestore.data.models.User
import com.project.morestore.presentation.mvpviews.FavoritesMvpView
import com.project.morestore.domain.presenters.FavoritesPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesSellersFragment :ListFragment(), FavoritesMvpView {
    private var adapter: FavoriteSellersAdapter by autoCleared()
    @Inject
    lateinit var favoritesPresenter: FavoritesPresenter
    private val presenter by moxyPresenter { favoritesPresenter }
    override val emptyList by lazy {
        EmptyList(
            R.drawable.img_favoritessellers_empty1,
            R.drawable.img_favoritessellers_empty2,
            R.drawable.img_favoritessellers_empty3,
            requireContext().getString(R.string.favorite_sellersListEmpty_message),
            requireContext().getString(R.string.favorite_listEmpty_actionText)
        )
    }
    override val list by lazy {
        RecyclerView(requireContext())
            .apply {
                layoutManager = GridLayoutManager(context, 2)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFavoriteSellers()


        view.setBackgroundResource(R.color.white)
        adapter = FavoriteSellersAdapter{ seller ->
            findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToSellerProfileFragment(user = seller, toReviews = false))

        }
        list.adapter = this.adapter

    }

    private fun getFavoriteSellers(){
        presenter.getSellersWishList()
    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val list = result as List<*>
        adapter.setItems(list as List<User>)

    }

    override fun error(message: String) {

    }

    override fun emptyList() {
        showEmptyList { findNavController().navigate(R.id.catalogFragment) }

    }

    override fun success() {

    }
}