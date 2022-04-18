package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
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
    private var productAdapter: ProductAdapter = ProductAdapter(null) { product ->
        findNavController().navigate(
            FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailsFragment(
                product = product,
                productId = null,
                isSeller = false
            )
        )

    }
    //by autoCleared()
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
        //showEmptyList { Toast.makeText(requireContext(), "123", Toast.LENGTH_SHORT).show() }
        /*productAdapter = ProductAdapter(null) { product ->
            findNavController().navigate(
                FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailsFragment(
                    product = product,
                    productId = null,
                    isSeller = false
                )
            )

        }*/
        //list.adapter = productAdapter
        //view.findViewById<RecyclerView>(-1).adapter = productAdapter
        presenter.getProductWishList()
        Log.d("mylog", "onViewCreated")
        list.setBackgroundResource(R.color.gray3)





    }

    override fun onDestroyView() {
        super.onDestroyView()

    }


    private fun initList(){
        /*list = RecyclerView(requireContext())
            .apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                //adapter = productAdapter
            }*/

    }

    override fun loading() {

    }

    override fun favoritesLoaded(list: List<*>) {
        showList()
        //this.list.adapter = productAdapter
        productAdapter.updateList(list as List<Product>)
        //Log.d("mylog", (this.list.adapter as ProductAdapter).itemCount.toString())
        //(this.list.adapter as ProductAdapter).notifyDataSetChanged()
        //Log.d("mylog", (this.list.parent == null).toString())



    }

    override fun error(message: String) {

    }

    override fun emptyList() {
        showEmptyList {findNavController().navigate(R.id.catalogFragment) }
    }

    override fun success() {

    }
}