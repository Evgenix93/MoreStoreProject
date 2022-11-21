package com.project.morestore.presentation.fragments.base

import android.os.Bundle
import android.view.View
import com.project.morestore.MainActivity
import moxy.MvpAppCompatFragment

open class FullscreenFragment : MvpAppCompatFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavBar(false)
    }
}