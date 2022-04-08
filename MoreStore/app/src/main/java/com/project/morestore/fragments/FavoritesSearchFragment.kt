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
import moxy.ktx.moxyPresenter

class FavoritesSearchFragment :ListFragment(), FavoritesMvpView {
    private val presenter by moxyPresenter { FavoritesPresenter(requireContext()) }
    private var adapter: SearchesAdapter by autoCleared()
        /*DeleteDialog(
            requireContext(),
            requireContext().getString(R.string.deleteDialog_title_pattern, it.title),
            requireContext().getString(R.string.deleteDialog_defaultMessage),
        ).show()*/

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
        this.adapter = SearchesAdapter { favoriteSearch ->
            findNavController().navigate(FavoritesFragmentDirections.actionFavoritesFragmentToEditFavoriteSearchFragment(favoriteSearchId = favoriteSearch.id))


        }
        RecyclerView(requireContext()).apply{
            layoutManager = LinearLayoutManager(requireContext())
            clipToPadding = false
            val vp = 8.dp
            setSpace(vp)
            setPadding(0, vp, 0, vp)
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            adapter = this@FavoritesSearchFragment.adapter
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFavoriteSearches()
        setClickListeners()
       /* showEmptyList {
            adapter.setItems(listOf(
                Search(
                    "Светлая зимняя одежда",
                    arrayOf(
                        "Женщинам",
                        "Верхняя одежда",
                        "Джинсы",
                        "Asos",
                        "Adidas",
                        "Абсолютно новое",
                        "Почти новое",
                        "38 (XXS)",
                        "42 (S)",
                        "Черный"
                    ),
                    Search.Notification.DISABLE
                ),
                Search(
                    "Для спорта (Adidas)",
                    arrayOf(
                        "Женщинам",
                        "Верхняя одежда",
                        "Джинсы",
                        "Asos",
                        "Adidas",
                        "Абсолютно новое",
                        "Почти новое",
                        "38 (XXS)",
                        "42 (S)",
                        "Черный"
                    ),
                    Search.Notification.WEEKLY
                ),
                Search(
                    "Красивое",
                    arrayOf(
                        "Женщинам",
                        "Верхняя одежда",
                        "Джинсы",
                        "Asos",
                        "Adidas",
                        "Абсолютно новое",
                        "Почти новое",
                        "38 (XXS)",
                        "42 (S)",
                        "Черный"
                    ),
                    Search.Notification.DAYLY
                )
            ))
            showList()
        }*/
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

    override fun favoritesLoaded(list: List<*>) {
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