package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.project.morestore.R
import com.project.morestore.adapters.PriceViewPagerAdapter
import com.project.morestore.databinding.FragmentRaiseProductBinding
import com.project.morestore.dialogs.MenuBottomDialogFragment
import com.project.morestore.models.PaymentUrl
import com.project.morestore.mvpviews.RaiseProductView
import com.project.morestore.presenters.OnboardingPresenter
import com.project.morestore.presenters.RaiseProductPresenter
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class RaiseProductFragment: MvpAppCompatFragment(R.layout.fragment_raise_product), RaiseProductView {
    private val binding: FragmentRaiseProductBinding by viewBinding()
    private var pricesViewPagerAdapter: PriceViewPagerAdapter by autoCleared()
    @Inject
    lateinit var raiseProductPresenter: RaiseProductPresenter
    private val presenter by moxyPresenter { raiseProductPresenter}
    private val args: RaiseProductFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initPricesViewPager()
        setClickListeners()
    }

    private fun initPricesViewPager(){
        pricesViewPagerAdapter = PriceViewPagerAdapter(this)
        binding.pricesViewPager.adapter = pricesViewPagerAdapter
        TabLayoutMediator(binding.daysTabLayout, binding.pricesViewPager){tab,position ->
            when (position){
                0 -> {
                    tab.text = getString(R.string.for1_day)
                }
                1 -> {
                    tab.text = getString(R.string.for7_days)
                }
            }}.attach()

        binding.pricesViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                if(position == 0)
                binding.backgroundView.background = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.raise_product_background
                )
                else
                    binding.backgroundView.background = AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.raise_product_background2
                    )
            }
        } )
    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Получите больше просмотров"
        binding.toolbar.backIcon.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun setClickListeners(){
        binding.raiseProductButton.setOnClickListener {
          presenter.raiseProduct(binding.pricesViewPager.currentItem, args.productId)
        }
    }

    override fun loading(isLoading: Boolean) {

    }


    override fun payment(paymentUrl: PaymentUrl) {
        binding.raiseProductButton.isVisible = false
        binding.paymentWebView.isVisible = true
        binding.paymentWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return if (request?.url.toString().contains("success")) {
                    view?.isVisible = false
                    val tariff = binding.pricesViewPager.currentItem + 1
                    findNavController().navigate(RaiseProductFragmentDirections.actionRaiseProductFragmentToSuccessPromoteFragment(
                        tariff
                    ))
                    true
                } else {
                    false
                }
            }

        }
        binding.paymentWebView.settings.userAgentString = "Chrome/56.0.0.0 Mobile"
        binding.paymentWebView.settings.javaScriptEnabled = true
        binding.paymentWebView.loadUrl(paymentUrl.formUrl)
    }

    override fun error(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

}