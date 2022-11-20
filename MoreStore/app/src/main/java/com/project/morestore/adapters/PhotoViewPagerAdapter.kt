package com.project.morestore.adapters


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.fragments.PhotoFragment


class PhotoViewPagerAdapter(fragment: Fragment, private val isSold: Boolean, val onClick: (String) -> Unit): FragmentStateAdapter(fragment) {
    private var list = listOf<String>()
    override fun getItemCount(): Int {
        return list.size

    }

    override fun createFragment(position: Int): Fragment {
        return PhotoFragment.createInstance(photo = list[position], isSold = isSold) { onClick(list[position]) }
        }



    fun updateList(newList: List<String>){
        list = newList
    }

}