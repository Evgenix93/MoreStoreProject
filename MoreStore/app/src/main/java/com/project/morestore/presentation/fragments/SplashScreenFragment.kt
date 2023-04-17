package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
                Log.d("mylog", "extras: ${requireActivity().intent?.extras}")
                val id = requireActivity().intent?.getStringExtra(MessagingService.ID)
                val idLong = requireActivity().intent?.getLongExtra(MessagingService.ID, -1L)
                val pushType = requireActivity().intent?.getStringExtra(MessagingService.PUSH_TYPE)
                Log.d("mylog", "orderId: $id")
                Log.d("mylog", "orderIdLong: $idLong")
                when (pushType) {
                   MessagingService.ORDER_NEW_STATUS_PUSH -> {
                       requireActivity().intent = null
                       findNavController().navigate(
                           SplashScreenFragmentDirections.actionSplashScreenFragmentToOrderDetailsFragment(
                               orderId = id?.toLong() ?: idLong ?: -1
                           )
                       )
                   }

                    MessagingService.NEW_PRICE_PUSH, MessagingService.NEW_PRODUCT_PUSH, MessagingService.PRODUCT_SOLD_PUSH -> {
                        requireActivity().intent = null
                        findNavController().navigate(
                            SplashScreenFragmentDirections.actionSplashScreenFragmentToProductDetailsFragment(productId = id ?: idLong.toString(),
                                product = null, isSeller = false )
                        )
                    }

                    else -> findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToMainFragment())
                }
            }
            else findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToFirstLaunchFragment())
        }
    }

    override fun loaded(result: Any) {
        if(result is Boolean){
            navigate(result)

        }
        if(result is String){
            Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show()
        }
    }
}