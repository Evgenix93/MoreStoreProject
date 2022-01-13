package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.mvpviews.AuthMvpView
import com.project.morestore.presenters.AuthPresenter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class MainFragment: MvpAppCompatFragment(R.layout.fragment_main), AuthMvpView {
    private val presenter by moxyPresenter { AuthPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavBar()
        presenter.loadOnBoardingViewed()
    }

    private fun showBottomNavBar(){
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(true)
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {

    }

    override fun loading() {

    }

    override fun showOnBoarding() {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToOnboarding1Fragment())

    }
}