package com.project.morestore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFavoritesBinding
import com.project.morestore.databinding.TabCategoryBinding
import com.project.morestore.fragments.base.BottomNavigationFragment
import com.project.morestore.util.setSelectListener

class FavoritesFragment :BottomNavigationFragment(){
    private lateinit var views :FragmentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFavoritesBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        indicateTabAt(-1)
        with(views){
            toolbar.title.setText(R.string.favorites_container_title)
            toolbar.root.also{
                it.setNavigationOnClickListener { findNavController().popBackStack() }
                it.inflateMenu(R.menu.menu_favorites)
            }
            tabs.addTab(tabs.newTab().fill(R.drawable.sel_tshirt, "Товары", 11))
            tabs.addTab(tabs.newTab().fill(R.drawable.sel_tag, "Бренды", 2))
            tabs.addTab(tabs.newTab().fill(R.drawable.sel_users, "Продавцы", 2))
            tabs.addTab(tabs.newTab().fill(R.drawable.sel_search_small, "Поиски", 2))
            tabs.setSelectListener {
                when(it.position){
                    0 -> showFragment(FavoritesGoodsFragment())
                    1 -> showFragment(FavoritesBrandsFragment())
                    2 -> showFragment(FavoritesSellersFragment())
                    else -> showFragment(FavoritesSearchFragment())
                }
            }
        }
        showFragment(FavoritesGoodsFragment())
    }

    private fun TabLayout.Tab.fill(@DrawableRes drawableId: Int, text: String, count: Int = 0) :TabLayout.Tab {
        TabCategoryBinding.inflate(this@FavoritesFragment.layoutInflater, this.view, false)
            .apply {
                customView = root
                title.text = text

                if (drawableId == 0) icon.visibility = View.GONE
                else icon.setImageResource(drawableId)

                if (count == 0) this.count.visibility = View.GONE
                else this.count.text = count.toString()
            }
        return this
    }

    private fun showFragment(listFragment :ListFragment){
        val tag = listFragment::class.simpleName
        val fragment = childFragmentManager.findFragmentByTag(tag)
        childFragmentManager.beginTransaction()
            .replace(R.id.container, fragment ?: listFragment, tag)
            .commit()
    }
}