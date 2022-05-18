package com.project.morestore.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.fragments.PhotoFragment
import com.project.morestore.models.ProductPhoto

class PhotoViewPagerAdapter(fragment: Fragment, private val isSold: Boolean, val onClick: (String) -> Unit): FragmentStateAdapter(fragment) {
    private var list = listOf<String>()
    override fun getItemCount(): Int {
        return list.size

    }

    override fun createFragment(position: Int): Fragment {
        return PhotoFragment{onClick(list[position])}.apply {
            arguments = Bundle().apply { putString("photo", list[position])
            putBoolean(PhotoFragment.IS_SOLD, isSold)}
        }

    }

    fun updateList(newList: List<String>){
        list = newList
    }

}