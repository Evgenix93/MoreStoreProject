package com.project.morestore.fragments.orders.history;

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.adapters.cart.OrdersHistoryAdapter
import com.project.morestore.databinding.FragmentOrdersHistoryBinding
import com.project.morestore.models.slidermenu.OrdersSliderMenu
import com.project.morestore.presenters.toolbar.cart.ToolbarCartPresenter
import com.project.morestore.presenters.toolbar.cart.ToolbarCartView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class OrdersHistoryFragment
    : MvpAppCompatFragment(R.layout.fragment_orders_history), OrdersHistoryView, ToolbarCartView {

    private val presenter by moxyPresenter { OrdersHistoryPresenter(requireContext()) }
    private val toolbarPresenter by moxyPresenter {
        ToolbarCartPresenter(requireContext(), OrdersSliderMenu.ORDERS_HISTORY)
    }
    private val binding: FragmentOrdersHistoryBinding by viewBinding()

    ///////////////////////////////////////////////////////////////////////////
    //                      view
    ///////////////////////////////////////////////////////////////////////////

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNav()
        initToolbar()
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      toolbar cart view
    ///////////////////////////////////////////////////////////////////////////

    override fun navigate(pageId: Int?) {
        when (pageId) {
            null -> {
                findNavController().popBackStack()
            }
            R.id.ordersCartFragment -> {
                findNavController().navigate(OrdersHistoryFragmentDirections.actionOrdersHistoryFragmentToOrdersCartFragment())
            }
            R.id.ordersActiveFragment -> {
                findNavController().navigate(OrdersHistoryFragmentDirections.actionOrdersHistoryFragmentToOrdersActiveFragment())
            }
        }
    }

    override fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>) {
        binding.toolbar.sliderMenu.adapter = adapter
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      OrdersHistoryView
    ///////////////////////////////////////////////////////////////////////////

    override fun initOrdersHistory(adapter: OrdersHistoryAdapter) {
        binding.ordersHistoryRecyclerView.adapter = adapter
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
    }
}