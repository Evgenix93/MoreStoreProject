package com.project.morestore.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.fragments.MainViewPagerFragment
import com.project.morestore.models.Banner

class MainFragmenViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private var list = listOf<Banner>()
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return MainViewPagerFragment(list[position])
        }

    fun updateList(newList: List<Banner>){
        list = newList
        notifyDataSetChanged()

    }
    }

