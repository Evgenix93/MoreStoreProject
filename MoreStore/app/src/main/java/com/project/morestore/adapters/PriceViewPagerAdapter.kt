package com.project.morestore.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.morestore.fragments.PriceFragment

class PriceViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
      return  PriceFragment().apply { arguments = Bundle().apply { putBoolean(PriceFragment.FOR_ONE_DAY, position == 0) } }
    }
}