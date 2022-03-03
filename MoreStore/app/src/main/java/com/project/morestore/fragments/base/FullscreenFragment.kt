package com.project.morestore.fragments.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.project.morestore.MainActivity

open class FullscreenFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavBar(false)
    }
}