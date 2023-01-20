package com.project.morestore.presentation.fragments.orders.active

import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.presentation.adapters.SliderMenuAdapter
import com.project.morestore.presentation.adapters.cart.OrdersAdapter
import com.project.morestore.databinding.FragmentOrdersBinding
import com.project.morestore.presentation.dialogs.YesNoDialog
import com.project.morestore.presentation.fragments.ChatFragment
import com.project.morestore.data.models.Chat
import com.project.morestore.data.models.PaymentUrl
import com.project.morestore.data.models.User
import com.project.morestore.data.models.cart.OrderItem
import com.project.morestore.data.models.slidermenu.OrdersSliderMenu
import com.project.morestore.domain.presenters.toolbar.cart.ToolbarCartPresenter
import com.project.morestore.presentation.mvpviews.ToolbarCartView

import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject


@AndroidEntryPoint
class OrdersActiveFragment
    : MvpAppCompatFragment(R.layout.fragment_orders), OrdersActiveView, ToolbarCartView {

    @Inject
    lateinit var toolbarPresenterRef: ToolbarCartPresenter
    @Inject
    lateinit var orderActivePresenterRef: OrdersActivePresenter
    private val toolBarCartPresenter by moxyPresenter {
        toolbarPresenterRef
    }
    private val orderActivePresenter by moxyPresenter { orderActivePresenterRef }
    private val binding: FragmentOrdersBinding by viewBinding()

    ///////////////////////////////////////////////////////////////////////////
    //                      view
    ///////////////////////////////////////////////////////////////////////////

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBarCartPresenter.initMenu(OrdersSliderMenu.ORDERS)
        showBottomNav()
        initToolbar()

    }

    ///////////////////////////////////////////////////////////////////////////
    //                      toolbar cart view
    ///////////////////////////////////////////////////////////////////////////

    override fun navigate(pageId: Int?) {
        val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
        when (pageId) {
            null -> {
                findNavController().popBackStack()
            }
            R.id.ordersCartFragment -> {
                findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToOrdersCartFragment(),
                    navOptions)
            }
            R.id.ordersHistoryFragment -> {
                findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToOrdersHistoryFragment(), navOptions)
            }
            R.id.salesActiveFragment ->{
                findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToSalesActiveFragment(), navOptions)
            }
            R.id.salesHistoryFragment -> {
                findNavController().navigate(R.id.salesHistoryFragment, null, navOptions)
            }
        }
    }

    override fun navigate(productId: Long) {
        findNavController().navigate(
            OrdersActiveFragmentDirections
                .actionOrdersActiveFragmentToOrderProblemsFragment(productId)
        )
    }

    override fun navigate(user: User) {
        findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToSellerProfileFragment(user = user, toReviews = false))
    }

    override fun navigate(order: OrderItem) {
        findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToOrderDetailsFragment(order))
    }

    override fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>) {
        binding.toolbar.sliderMenu.adapter = adapter
        binding.toolbar.sliderMenu.scrollToPosition(1)
    }

    ///////////////////////////////////////////////////////////////////////////
    //                  active orders view
    ///////////////////////////////////////////////////////////////////////////

    override fun initActiveOrders(adapter: OrdersAdapter) {
        binding.ordersRecyclerView.adapter = adapter

    }

    override fun showAcceptOrderDialog(acceptDialog: YesNoDialog) {
        if (isAdded) {
            acceptDialog.show(parentFragmentManager, YesNoDialog.TAG)
        }
    }

    override fun showMessage(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loading(isLoading: Boolean) {
        showLoading(isLoading)
    }

    override fun navigateToChat(userId: Long, productId: Long) {
        findNavController().navigate(
            R.id.chatFragment,
            bundleOf(
                ChatFragment.USER_ID_KEY to userId,
                ChatFragment.PRODUCT_ID_KEY to productId,
                ChatFragment.FROM_ORDERS to true,
                Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
            )
        )
    }

    override fun payment(url: PaymentUrl, orderId: Long) {
        binding.loader.isVisible = false
        binding.webView.isVisible = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return if (request?.url.toString().contains("success")) {
                    view?.isVisible = false
                    findNavController().navigate(
                        OrdersActiveFragmentDirections.actionOrdersActiveFragmentToSuccessOrderPaymentFragment(orderId = orderId))
                    true
                }else if(request?.url.toString().contains("failed")) {
                    showMessage("Ошибка оплаты")
                    loading(false)
                    true
                }else {
                    false
                }
            }

        }
        binding.webView.settings.userAgentString = "Chrome/56.0.0.0 Mobile"
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(url.formUrl)
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    private fun showBottomNav() {
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun initToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            toolBarCartPresenter.onBackClick();
        }

        binding.toolbar.toolbarLike.setOnClickListener{
            findNavController().navigate(R.id.favoritesFragment)
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.loader.isVisible = isLoading
    }
}