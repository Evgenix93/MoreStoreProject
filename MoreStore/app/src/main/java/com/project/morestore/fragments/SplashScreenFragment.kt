package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.presenters.MainPresenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class SplashScreenFragment: MvpAppCompatFragment(R.layout.fragment_splash_screen), MainMvpView {
    private var job: Job = Job()
    private val presenter by moxyPresenter { MainPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavBar()
        presenter.checkToken()
       // job = lifecycleScope.launch {
          //  presenter.checkToken()
         //   delay(2000)
         //   findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToFirstLaunchFragment())

        //}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel()
    }


    private fun hideBottomNavBar(){
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(false)
    }

    private fun navigate(isLogined: Boolean){
        job = lifecycleScope.launch {
            delay(2000)
            if(isLogined)
                findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToMainFragment())
            else findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToFirstLaunchFragment())
        }
    }

    override fun loaded(result: Any) {
        if(result is Boolean){
            navigate(result)

        }

    }

    override fun loading() {

    }

    override fun error(message: String) {

    }

    override fun showOnBoarding() {

    }

    override fun loadedSuggestions(list: List<String>) {

    }

    override fun success() {

    }
}