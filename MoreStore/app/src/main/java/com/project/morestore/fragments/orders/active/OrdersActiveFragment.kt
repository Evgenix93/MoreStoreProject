package com.project.morestore.fragments.orders.active

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.MainActivity
import com.project.morestore.R
import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.adapters.cart.OrdersAdapter
import com.project.morestore.databinding.FragmentOrdersBinding
import com.project.morestore.dialogs.YesNoDialog
import com.project.morestore.fragments.ChatFragment
import com.project.morestore.models.Chat
import com.project.morestore.models.slidermenu.OrdersSliderMenu
import com.project.morestore.models.slidermenu.SliderMenu
import com.project.morestore.presenters.toolbar.cart.ToolbarCartPresenter
import com.project.morestore.presenters.toolbar.cart.ToolbarCartView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter


class OrdersActiveFragment
    : MvpAppCompatFragment(R.layout.fragment_orders), OrdersActiveView, ToolbarCartView {

    private val presenter by moxyPresenter { OrdersActivePresenter(requireContext()) }
    private val toolbarPresenter by moxyPresenter {
        ToolbarCartPresenter(requireContext(), OrdersSliderMenu.ORDERS)
    }
    private val binding: FragmentOrdersBinding by viewBinding()

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
                findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToOrdersCartFragment())
            }
            R.id.ordersHistoryFragment -> {
                findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToOrdersHistoryFragment())
            }
        }
    }

    override fun navigate(productId: Long) {
        findNavController().navigate(
            OrdersActiveFragmentDirections
                .actionOrdersActiveFragmentToOrderProblemsFragment(productId)
        )
    }

    override fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>) {
        binding.toolbar.sliderMenu.adapter = adapter
    }

    ///////////////////////////////////////////////////////////////////////////
    //                  active orders view
    ///////////////////////////////////////////////////////////////////////////

    override fun initActiveOrders(adapter: OrdersAdapter) {
        binding.ordersRecyclerView.adapter = adapter
        //(binding.toolbar.sliderMenu.adapter as SliderMenuAdapter<SliderMenu<*>>).changeOrdersItemsSize(adapter.itemCount)
    }

    override fun showAcceptOrderDialog(acceptDialog: YesNoDialog) {
        if (isAdded) {
            acceptDialog.show(parentFragmentManager, YesNoDialog.TAG)
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun loading() {

    }

    override fun navigateToChat(userId: Long, productId: Long) {
        findNavController().navigate(
            R.id.chatFragment,
            bundleOf(
                ChatFragment.USER_ID_KEY to userId,
                ChatFragment.PRODUCT_ID_KEY to productId,
                Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
            )
        )
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      private
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