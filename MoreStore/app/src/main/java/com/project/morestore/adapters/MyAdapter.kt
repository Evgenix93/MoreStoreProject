package com.project.morestore.adapters

import android.widget.Adapter
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.project.morestore.fragments.BuyersFragment
import com.project.morestore.fragments.SellersFragment

class MyAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
       return if (position == 0)
             BuyersFragment()
        else
            SellersFragment()
    }
}