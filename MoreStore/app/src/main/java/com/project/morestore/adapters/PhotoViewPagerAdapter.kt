package com.project.morestore.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.fragments.PhotoFragment

class PhotoViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private var list = listOf<String>()
    override fun getItemCount(): Int {
        return list.size

    }

    override fun createFragment(position: Int): Fragment {
        return PhotoFragment().apply {
            arguments = Bundle().apply { putString("photo", list[position]) }
        }


    }

    fun updateList(newList: List<String>){
        list = newList
    }

}