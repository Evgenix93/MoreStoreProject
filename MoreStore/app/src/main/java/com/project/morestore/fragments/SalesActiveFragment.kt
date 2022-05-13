package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SalesAdapter
import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.databinding.FragmentOrdersBinding
import com.project.morestore.models.*
import com.project.morestore.models.cart.CartItem
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.slidermenu.OrdersSliderMenu
import com.project.morestore.models.slidermenu.SliderMenu
import com.project.morestore.mvpviews.SalesMvpView
import com.project.morestore.presenters.SalesPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class SalesActiveFragment: MvpAppCompatFragment(R.layout.fragment_orders), SalesMvpView {
    private val binding: FragmentOrdersBinding by viewBinding()
    private var salesAdapter: SalesAdapter by autoCleared()
    private var menuAdapter: SliderMenuAdapter<OrdersSliderMenu> by autoCleared()
    private val presenter by moxyPresenter { SalesPresenter(requireContext()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initMenuList()
        initSalesList()
        getSales()
    }

    private fun initSalesList(){
       binding.ordersRecyclerView.apply {
           adapter = SalesAdapter(false,{
             findNavController().navigate(SalesActiveFragmentDirections.actionSalesActiveFragmentToDealPlaceFragment(it))
           }, {
            presenter.acceptOrderPlace(it)
           }, {userId, productId ->
               findNavController().navigate(
                   R.id.chatFragment,
                   bundleOf(
                       ChatFragment.USER_ID_KEY to userId,
                       ChatFragment.PRODUCT_ID_KEY to productId,
                       Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                   )
               )
           }).also{salesAdapter = it}
           layoutManager = LinearLayoutManager(requireContext())
       }
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
                    true,
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
                    false,
                    OrdersSliderMenu.SALES_HISTORY
                )
            )
        ) {
            when (it) {
                OrdersSliderMenu.SALES -> {

                }
                OrdersSliderMenu.SALES_HISTORY -> {
                    findNavController().navigate(R.id.salesHistoryFragment)
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
    }

    private fun getSales(){
      presenter.getSales(false)
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun initToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateSalesList(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>){
        salesAdapter.updateList(sales, addresses, users)
    }

    override fun onSalesLoaded(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>) {
        Log.d("MyDebug", "onSalesLoaded")
        updateSalesList(sales, addresses, users)
    }

    override fun onUserLoaded() {

    }

    override fun onError(message: String) {
        showToast(message)
    }

    override fun onDealPlaceAdded() {

    }

    override fun onDealPlaceAccepted() {
        getSales()
        showToast("Адрес подтвержден")
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
        menuAdapter.changeSalesHistorySize(inactiveSales.size)
    }
}