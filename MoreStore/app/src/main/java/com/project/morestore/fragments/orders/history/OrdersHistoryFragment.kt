package com.project.morestore.fragments.orders.history;

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.adapters.cart.OrdersHistoryAdapter
import com.project.morestore.databinding.FragmentOrdersHistoryBinding
import com.project.morestore.fragments.orders.cart.OrdersCartFragmentDirections
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
        showLoading(true)
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
            R.id.salesActiveFragment ->{
                findNavController().navigate(OrdersHistoryFragmentDirections.actionOrdersHistoryFragmentToSalesActiveFragment())
            }
            R.id.salesHistoryFragment -> findNavController().navigate(R.id.salesHistoryFragment)
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

    private fun showLoading(isLoading: Boolean){
        binding.loader.isVisible = isLoading
    }
}