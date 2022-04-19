package com.project.morestore.fragments.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import com.project.morestore.MainActivity
import moxy.MvpAppCompatFragment

open class FullscreenMvpFragment: MvpAppCompatFragment {
    constructor() :super()
    constructor(contentLayoutId :Int) :super()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavBar(false)
    }
}