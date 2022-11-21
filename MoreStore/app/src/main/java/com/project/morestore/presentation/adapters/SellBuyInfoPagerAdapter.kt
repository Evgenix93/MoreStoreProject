package com.project.morestore.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.presentation.fragments.BuyersFragment
import com.project.morestore.presentation.fragments.SellersFragment

class SellBuyInfoPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
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