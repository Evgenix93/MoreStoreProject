package com.project.morestore.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.fragments.PhotoFragment
import com.project.morestore.models.ProductPhoto

class PhotoViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private var list = listOf<ProductPhoto>()
    override fun getItemCount(): Int {
        return list.size

    }

    override fun createFragment(position: Int): Fragment {
        return PhotoFragment().apply {
            arguments = Bundle().apply { putString("photo", list[position].photo) }
        }


    }

    fun updateList(newList: List<ProductPhoto>){
        list = newList
    }

}