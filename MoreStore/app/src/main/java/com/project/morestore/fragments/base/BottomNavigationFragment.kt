package com.project.morestore.fragments.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.project.morestore.MainActivity
import moxy.MvpAppCompatFragment

open class BottomNavigationFragment :MvpAppCompatFragment {
    constructor(): super()
    constructor(@LayoutRes layoutId: Int): super(layoutId)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavBar(true)
    }

    fun indicateTabAt(index :Int){
        if(index == -1){
            (activity as MainActivity).hideBottomIndication()
        }
    }
}