package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.project.morestore.R
import com.project.morestore.databinding.FragmentFavoritesBinding
import com.project.morestore.databinding.TabCategoryBinding
import com.project.morestore.fragments.base.BottomNavigationFragment
import com.project.morestore.models.FavoriteSearch
import com.project.morestore.models.Product
import com.project.morestore.models.ProductBrand
import com.project.morestore.models.User
import com.project.morestore.mvpviews.FavoritesMvpView
import com.project.morestore.presenters.FavoritesPresenter
import com.project.morestore.util.setSelectListener
import moxy.ktx.moxyPresenter

class FavoritesFragment :BottomNavigationFragment(), FavoritesMvpView{
    private lateinit var views :FragmentFavoritesBinding
    private val presenter by moxyPresenter { FavoritesPresenter(requireContext()) }
    private  var selectedTab: TabLayout.Tab? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFavoritesBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //indicateTabAt(-1)
        with(views){
            toolbar.title.setText(R.string.favorites_container_title)
            toolbar.root.also{
                it.setNavigationOnClickListener { findNavController().popBackStack() }
                it.inflateMenu(R.menu.menu_favorites)
            }
            tabs.addTab(tabs.newTab().fill(R.drawable.sel_tshirt, "Товары", 0))
            tabs.addTab(tabs.newTab().fill(R.drawable.sel_tag, "Бренды", 0))
            tabs.addTab(tabs.newTab().fill(R.drawable.sel_users, "Продавцы", 0))
            tabs.addTab(tabs.newTab().fill(R.drawable.sel_search_small, "Поиски", 0))
            tabs.setSelectListener {
                Log.d("MyTab", "tab selected")
                selectedTab = it
                when (it.position) {
                    0 -> {
                        Log.d("MyTab", "selected tab showFavoritesGoods")
                        showFragment(FavoritesGoodsFragment())}
                    1 -> {
                        showFragment(FavoritesBrandsFragment())}
                    2 -> {
                        showFragment(FavoritesSellersFragment())}
                    else -> {
                        showFragment(FavoritesSearchFragment())}
                }
            }
        }
        getFavorites()
        if(selectedTab == null)
            showFragment(FavoritesGoodsFragment())
        else
            views.tabs.selectTab(selectedTab)
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

    private fun getFavorites(){
        presenter.getProductWishList()
        presenter.getBrandWishList()
        presenter.getSellersWishList()
        presenter.getFavoriteSearches()
    }

    override fun loading() {

    }

    override fun favoritesLoaded(list: List<*>) {
        when(list[0]){
            is Product -> {
                val countTextView = views.tabs.getTabAt(0)?.view?.findViewById<TextView>(R.id.count)
                countTextView?.isVisible = true
                countTextView?.text = list.size.toString()
            }
            is ProductBrand -> {
                val countTextView = views.tabs.getTabAt(1)?.view?.findViewById<TextView>(R.id.count)
                countTextView?.isVisible = true
                countTextView?.text = list.size.toString()
            }
            is User ->{
                val countTextView = views.tabs.getTabAt(2)?.view?.findViewById<TextView>(R.id.count)
                countTextView?.isVisible = true
                countTextView?.text = list.size.toString()
            }
            is FavoriteSearch -> {
                val countTextView = views.tabs.getTabAt(3)?.view?.findViewById<TextView>(R.id.count)
                countTextView?.isVisible = true
                countTextView?.text = list.size.toString()
            }


        }

    }

    override fun error(message: String) {

    }

    override fun emptyList() {

    }

    override fun success() {

    }
}