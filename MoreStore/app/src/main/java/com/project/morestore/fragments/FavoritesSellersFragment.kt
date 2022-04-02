package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R
import com.project.morestore.adapters.FavoriteSellersAdapter
import com.project.morestore.models.FavoriteSeller

class FavoritesSellersFragment :ListFragment() {
    private val adapter = FavoriteSellersAdapter()
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
                adapter = this@FavoritesSellersFragment.adapter
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showEmptyList {
            adapter.setItems(
                listOf(
                    FavoriteSeller(R.drawable.favorite_seller1, "Елена Белова", 4.8f),
                    FavoriteSeller(R.drawable.favorite_seller2, "Иван Домов", 4.5f),
                    FavoriteSeller(R.drawable.favorite_seller3, "Валентина Косова", 4.3f),
                    FavoriteSeller(R.drawable.favorite_seller4, "Матвей Мартынов", 5f),
                    FavoriteSeller(R.drawable.favorite_seller5, "Марина Трошева", 4.8f)
                )
            )
            showList()
        }
    }
}