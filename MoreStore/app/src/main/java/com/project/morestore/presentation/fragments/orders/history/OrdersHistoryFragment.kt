package com.project.morestore.presentation.fragments.orders.history;

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.presentation.adapters.SliderMenuAdapter
import com.project.morestore.presentation.adapters.cart.OrdersHistoryAdapter
import com.project.morestore.databinding.FragmentOrdersHistoryBinding
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
class OrdersHistoryFragment
    : MvpAppCompatFragment(R.layout.fragment_orders_history), OrdersHistoryView, ToolbarCartView {

    @Inject
    lateinit var _toolbarPresenter: ToolbarCartPresenter
    @Inject
    lateinit var _presenter: OrdersHistoryPresenter
    private val toolbarPresenter by moxyPresenter {
        _toolbarPresenter
    }
    private val presenter by moxyPresenter { _presenter }
    private val binding: FragmentOrdersHistoryBinding by viewBinding()

    ///////////////////////////////////////////////////////////////////////////
    //                      view
    ///////////////////////////////////////////////////////////////////////////

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarPresenter.initMenu(OrdersSliderMenu.ORDERS_HISTORY)
        showBottomNav()
        initToolbar()
        showLoading(true)
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
                findNavController().navigate(OrdersHistoryFragmentDirections.actionOrdersHistoryFragmentToOrdersCartFragment(), navOptions)
            }
            R.id.ordersActiveFragment -> {
                findNavController().navigate(OrdersHistoryFragmentDirections.actionOrdersHistoryFragmentToOrdersActiveFragment(), navOptions)
            }
            R.id.salesActiveFragment ->{
                findNavController().navigate(OrdersHistoryFragmentDirections.actionOrdersHistoryFragmentToSalesActiveFragment(), navOptions)
            }
            R.id.salesHistoryFragment -> findNavController().navigate(R.id.salesHistoryFragment, null, navOptions)
        }
    }

    override fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>) {
        binding.toolbar.sliderMenu.adapter = adapter
        binding.toolbar.sliderMenu.scrollToPosition(3)
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      OrdersHistoryView
    ///////////////////////////////////////////////////////////////////////////

    override fun initOrdersHistory(adapter: OrdersHistoryAdapter) {
        showLoading(false)
        binding.ordersHistoryRecyclerView.adapter = adapter
    }

    override fun showMessage(message: String) {
          showLoading(false)
    }

    override fun navigate(user: User) {
        findNavController().navigate(OrdersHistoryFragmentDirections.actionOrdersHistoryFragmentToSellerProfileFragment(user = user, toReviews = false))
    }

    override fun navigate(order: OrderItem) {
        findNavController().navigate(OrdersHistoryFragmentDirections.actionOrdersHistoryFragmentToOrderDetailsFragment(order))
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Private
    ///////////////////////////////////////////////////////////////////////////

    private fun showBottomNav() {
        (activity as MainActivity).showBottomNavBar(true)
    }

    private fun initToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            toolbarPresenter.onBackClick();
        }

        binding.toolbar.toolbarLike.setOnClickListener{
            findNavController().navigate(R.id.favoritesFragment)
        }
    }

    private fun showLoading(isLoading: Boolean){
        binding.loader.isVisible = isLoading
    }
}