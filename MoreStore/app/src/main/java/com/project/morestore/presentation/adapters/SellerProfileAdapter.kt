package com.project.morestore.presentation.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.presentation.fragments.SellerProductsFragment
import com.project.morestore.presentation.fragments.SellerReviewsFragment
import com.project.morestore.data.models.Product
import com.project.morestore.data.models.User

class SellerProfileAdapter(
    fragment: Fragment,
    val userId :Long
): FragmentStateAdapter(fragment) {
    lateinit var user: User
    var sellerProducts = listOf<Product>()
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
      return  if(position == 0)
            SellerProductsFragment().apply { arguments = Bundle().apply { putParcelableArray(PRODUCTS, sellerProducts.toTypedArray())  }}
        else
            SellerReviewsFragment(userId)
    }

    fun updateList(newList: List<Product>){
        sellerProducts = newList
        notifyDataSetChanged()
    }

    companion object{
        const val PRODUCTS = "products"
        const val USER = "user"
    }
}