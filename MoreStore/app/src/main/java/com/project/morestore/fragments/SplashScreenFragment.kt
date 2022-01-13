package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.morestore.MainActivity
import com.project.morestore.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenFragment: Fragment(R.layout.fragment_splash_screen) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavBar()
        lifecycleScope.launch {
            delay(2000)
            findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToFirstLaunchFragment())

        }
    }


    private fun hideBottomNavBar(){
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(false)
    }
}