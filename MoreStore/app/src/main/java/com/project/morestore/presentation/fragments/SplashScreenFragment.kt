package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.domain.presenters.MainPresenter
import com.project.morestore.domain.presenters.SplashScreenPresenter
import com.project.morestore.presentation.mvpviews.ResultLoadedMvpView
import com.project.morestore.util.MessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenFragment: MvpAppCompatFragment(R.layout.fragment_splash_screen), ResultLoadedMvpView {
    private var job: Job? = null
    @Inject
    lateinit var splashScreenPresenter: SplashScreenPresenter
    private val presenter by moxyPresenter { splashScreenPresenter }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavBar()
        presenter.checkToken()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job?.cancel()
    }


    private fun hideBottomNavBar(){
        val mainActivity = activity as MainActivity
        mainActivity.showBottomNavBar(false)
    }

    private fun navigate(isLogined: Boolean){
        job = lifecycleScope.launch {
            delay(2000)
            if(isLogined) {
                Log.d("mylog", "extras: ${requireActivity().intent?.getStringExtra(MessagingService.ORDER_KEY)}")
                val orderId = requireActivity().intent?.getStringExtra(MessagingService.ORDER_KEY)
                val orderIdLong = requireActivity().intent?.getLongExtra(MessagingService.ORDER_KEY, -1L)
                Log.d("mylog", "orderId: $orderId")
                if(orderId?.isNotEmpty() == true || (orderIdLong != null && orderIdLong != -1L)) {
                    requireActivity().intent = null
                    findNavController().navigate(
                        SplashScreenFragmentDirections.actionSplashScreenFragmentToOrderDetailsFragment(
                            orderId = orderId?.toLong() ?: orderIdLong ?: -1
                        )
                    )
                }
                else
                    findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToMainFragment())
            }
            else findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToFirstLaunchFragment())
        }
    }

    override fun loaded(result: Any) {
        if(result is Boolean){
            navigate(result)

        }
    }
}