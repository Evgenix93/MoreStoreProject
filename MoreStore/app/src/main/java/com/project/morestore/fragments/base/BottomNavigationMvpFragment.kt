package com.project.morestore.fragments.base

import android.os.Bundle
import android.view.View
import com.project.morestore.MainActivity
import moxy.MvpAppCompatFragment

open class BottomNavigationMvpFragment: MvpAppCompatFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavBar(true)
    }
}