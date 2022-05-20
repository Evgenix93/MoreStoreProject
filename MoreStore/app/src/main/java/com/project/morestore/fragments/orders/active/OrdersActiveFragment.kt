package com.project.morestore.fragments.orders.active

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import com.project.morestore.fragments.orders.cart.OrdersCartFragmentDirections
import com.project.morestore.models.User
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
                findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToOrdersCartFragment())
            }
            R.id.ordersHistoryFragment -> {
                findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToOrdersHistoryFragment())
            }
            R.id.salesActiveFragment ->{
                findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToSalesActiveFragment())
            }
            R.id.salesHistoryFragment -> findNavController().navigate(R.id.salesHistoryFragment)
        }
    }

    override fun navigate(productId: Long) {
        findNavController().navigate(
            OrdersActiveFragmentDirections
                .actionOrdersActiveFragmentToOrderProblemsFragment(productId)
        )
    }

    override fun navigate(user: User) {
        findNavController().navigate(OrdersActiveFragmentDirections.actionOrdersActiveFragmentToSellerProfileFragment(user, false))
    }

    override fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>) {
        binding.toolbar.sliderMenu.adapter = adapter
        binding.toolbar.sliderMenu.scrollToPosition(1)
    }

    ///////////////////////////////////////////////////////////////////////////
    //                  active orders view
    ///////////////////////////////////////////////////////////////////////////

    override fun initActiveOrders(adapter: OrdersAdapter) {
        showLoading(false)
        binding.ordersRecyclerView.adapter = adapter
        //(binding.toolbar.sliderMenu.adapter as SliderMenuAdapter<SliderMenu<*>>).changeOrdersItemsSize(adapter.itemCount)
    }

    override fun showAcceptOrderDialog(acceptDialog: YesNoDialog) {
        if (isAdded) {
            acceptDialog.show(parentFragmentManager, YesNoDialog.TAG)
        }
    }

    override fun showMessage(message: String) {
        showLoading(false)
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
                ChatFragment.FROM_ORDERS to true,
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

    private fun showLoading(isLoading: Boolean){
        binding.loader.isVisible = isLoading
    }
}