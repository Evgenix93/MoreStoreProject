package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SalesAdapter
import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.databinding.FragmentOrdersBinding
import com.project.morestore.models.DialogWrapper
import com.project.morestore.models.OfferedOrderPlace
import com.project.morestore.models.Order
import com.project.morestore.models.User
import com.project.morestore.models.cart.CartItem
import com.project.morestore.models.slidermenu.OrdersSliderMenu
import com.project.morestore.models.slidermenu.SliderMenu
import com.project.morestore.mvpviews.SalesMvpView
import com.project.morestore.presenters.SalesPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class SalesHistoryFragment: MvpAppCompatFragment(R.layout.fragment_orders), SalesMvpView {
    private val binding: FragmentOrdersBinding by viewBinding()
    private var salesAdapter: SalesAdapter by autoCleared()
    private var menuAdapter: SliderMenuAdapter<OrdersSliderMenu> by autoCleared()
    private val presenter by moxyPresenter { SalesPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenuList()
        initSalesHistoryList()
        getSalesHistory()
    }

    private fun initMenuList(){
        binding.toolbar.sliderMenu.adapter = SliderMenuAdapter(
            listOf(
                SliderMenu(
                    R.drawable.ic_cart,
                    requireContext().getString(R.string.orders_menu_cart),
                    0u,
                    false,
                    OrdersSliderMenu.CART
                ),
                SliderMenu(
                    R.drawable.ic_package,
                    requireContext().getString(R.string.orders_menu_orders),
                    0u,
                    false,
                    OrdersSliderMenu.ORDERS
                ),
                SliderMenu(
                    R.drawable.ic_coin,
                    requireContext().getString(R.string.sale),
                    0u,
                    false,
                    OrdersSliderMenu.SALES
                ),
                SliderMenu(
                    R.drawable.ic_reverse_clockwise,
                    requireContext().getString(R.string.orders_menu_purchase_history),
                    0u,
                    false,
                    OrdersSliderMenu.ORDERS_HISTORY
                ),
                SliderMenu(
                    R.drawable.ic_clockwise,
                    requireContext().getString(R.string.orders_menu_sales_history),
                    0u,
                    true,
                    OrdersSliderMenu.SALES_HISTORY
                )
            )
        ) {
            when (it) {
                OrdersSliderMenu.SALES -> {
                  findNavController().navigate(R.id.salesActiveFragment)
                }
                OrdersSliderMenu.SALES_HISTORY -> {
                }
                OrdersSliderMenu.CART -> {
                    findNavController().navigate(R.id.ordersCartFragment)
                }
                OrdersSliderMenu.ORDERS_HISTORY -> {
                    findNavController().navigate(R.id.ordersHistoryFragment)
                }
                OrdersSliderMenu.ORDERS -> {
                    findNavController().navigate(R.id.ordersActiveFragment)
                }
            }
        }.also{menuAdapter = it}
        binding.toolbar.sliderMenu.scrollToPosition(4)
    }

    private fun initSalesHistoryList(){
        binding.ordersRecyclerView.apply {
            adapter = SalesAdapter(true,{},{},{_,_ ->}, acceptDeal = {}, cancelDeal = {}).also{salesAdapter = it}
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun getSalesHistory(){
      binding.loader.isVisible = true
      presenter.getSales(true)
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onSalesLoaded(
        sales: List<Order>,
        addresses: List<OfferedOrderPlace>,
        users: List<User?>,
        dialogs: List<DialogWrapper>
    ) {
        binding.loader.isVisible = false
        salesAdapter.updateList(sales, addresses, users, dialogs)
    }

    
    override fun onError(message: String) {
        binding.loader.isVisible = false
        showToast(message)
    }

    override fun onDealPlaceAdded() {
        TODO("Not yet implemented")
    }

    override fun onDealPlaceAccepted() {
        TODO("Not yet implemented")
    }

    override fun onItemsLoaded(
        cartItems: List<CartItem>,
        activeOrders: List<Order>,
        activeSales: List<Order>,
        inactiveOrders: List<Order>,
        inactiveSales: List<Order>
    ) {
        menuAdapter.changeCartItemsSize(cartItems.size)
        menuAdapter.changeOrdersItemsSize(activeOrders.size)
        menuAdapter.changeSalesItemsSize(activeSales.size)
        menuAdapter.changeOrderHistorySize(inactiveOrders.size)
        menuAdapter.changeSalesHistorySize(inactiveSales.size)
    }

    override fun onDealStatusChanged() {
        TODO("Not yet implemented")
    }
}