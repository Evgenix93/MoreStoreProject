package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.project.morestore.MainActivity
import com.project.morestore.R

class MainFragment: Fragment(R.layout.fragment_main) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavBar()
    }

    private fun showBottomNavBar(){
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(true)
    }
}