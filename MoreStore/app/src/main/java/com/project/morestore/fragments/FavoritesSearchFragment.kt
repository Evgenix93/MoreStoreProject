package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.project.morestore.R
import com.project.morestore.adapters.SearchesAdapter
import com.project.morestore.dialogs.DeleteDialog
import com.project.morestore.models.FavoriteSearch
import com.project.morestore.models.Search
import com.project.morestore.mvpviews.FavoritesMvpView
import com.project.morestore.presenters.FavoritesPresenter
import com.project.morestore.util.autoCleared
import com.project.morestore.util.dp
import com.project.morestore.util.setSpace
import dagger.hilt.android.AndroidEntryPoint
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesSearchFragment :ListFragment(), FavoritesMvpView {
    @Inject
    lateinit var favoritesPresenter: FavoritesPresenter
    private val presenter by moxyPresenter { favoritesPresenter }
    private var adapter: SearchesAdapter by autoCleared()

    override val emptyList by lazy {
        EmptyList(
            R.drawable.img_favoritessearch_empty1,
            R.drawable.img_favoritessearch_empty2,
            R.drawable.img_favoritessearch_empty3,
            requireContext().getString(R.string.favorite_searchListEmpty_message),
            requireContext().getString(R.string.favorite_listEmpty_actionText)
        )
    }

    override val list by lazy{
        RecyclerView(requireContext()).apply{
            layoutManager = LinearLayoutManager(requireContext())
            clipToPadding = false
            val vp = 8.dp
            setSpace(vp)
            setPadding(0, vp, 0, vp)
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFavoriteSearches()
        setClickListeners()

        view.setBackgroundResource(R.color.white)
        this.adapter = SearchesAdapter { favoriteSearch ->
            findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToEditFavoriteSearchFragment(favoriteSearchId = favoriteSearch.id))


        }
        list.adapter = this.adapter

    }

    private fun getFavoriteSearches(){
        presenter.getFavoriteSearches()

    }

    private fun setClickListeners(){
        requireView().findViewById<MaterialButton>(R.id.addSearchBtn).setOnClickListener {
            findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToEditFavoriteSearchFragment())
        }

    }

    override fun loading() {

    }

    override fun loaded(result: Any) {
        val list = result as List<*>
        adapter.setItems(list as List<FavoriteSearch>)
        showList()
        showBtn(true)

    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }


    override fun emptyList() {
        showEmptyList { findNavController().navigate(R.id.catalogFragment) }

    }

    override fun success() {

    }
}