package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.morestore.R

class FavoritesBrandsFragment :ListFragment() {
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
                layoutManager = LinearLayoutManager(requireContext())
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showEmptyList { Toast.makeText(requireContext(), "123", Toast.LENGTH_SHORT).show() }
    }
}