package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.SalesAdapter
import com.project.morestore.presentation.adapters.SliderMenuAdapter
import com.project.morestore.databinding.FragmentOrdersBinding
import com.project.morestore.data.models.DialogWrapper
import com.project.morestore.data.models.OfferedOrderPlace
import com.project.morestore.data.models.Order
import com.project.morestore.data.models.User
import com.project.morestore.data.models.cart.CartItem
import com.project.morestore.data.models.slidermenu.OrdersSliderMenu
import com.project.morestore.data.models.slidermenu.SliderMenu
import com.project.morestore.domain.presenters.SalesHistoryPresenter
import com.project.morestore.presentation.mvpviews.SalesMvpView
import com.project.morestore.domain.presenters.SalesPresenter
import com.project.morestore.domain.presenters.toolbar.cart.ToolbarCartPresenter
import com.project.morestore.presentation.mvpviews.SalesHistoryMvpView
import com.project.morestore.presentation.mvpviews.ToolbarCartView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class SalesHistoryFragment: MvpAppCompatFragment(R.layout.fragment_orders), SalesHistoryMvpView, ToolbarCartView {
    private val binding: FragmentOrdersBinding by viewBinding()
    private var salesAdapter: SalesAdapter by autoCleared()
    private var menuAdapter: SliderMenuAdapter<OrdersSliderMenu> by autoCleared()
    @Inject
    lateinit var salesPresenter: SalesHistoryPresenter
    @Inject
    lateinit var toolBarPresenterRef: ToolbarCartPresenter
    private val presenter by moxyPresenter { salesPresenter }
    private val toolBarPresenter by moxyPresenter { toolBarPresenterRef }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBarPresenter.initMenu(OrdersSliderMenu.SALES_HISTORY)
        initToolbar()
        //initMenuList()
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
            val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
            when (it) {
                OrdersSliderMenu.SALES -> {
                  findNavController().navigate(R.id.salesActiveFragment, null, navOptions)
                }
                OrdersSliderMenu.SALES_HISTORY -> {
                }
                OrdersSliderMenu.CART -> {
                    findNavController().navigate(R.id.ordersCartFragment, null, navOptions)
                }
                OrdersSliderMenu.ORDERS_HISTORY -> {
                    findNavController().navigate(R.id.ordersHistoryFragment, null, navOptions)
                }
                OrdersSliderMenu.ORDERS -> {
                    findNavController().navigate(R.id.ordersActiveFragment, null, navOptions)
                }
            }
        }.also{menuAdapter = it}
        binding.toolbar.sliderMenu.scrollToPosition(4)
    }

    private fun initSalesHistoryList(){
        binding.ordersRecyclerView.apply {
            adapter = SalesAdapter(true,{},{},{_,_ ->}, acceptDeal = {}, cancelDeal = {}, onProfileClick =
            {user -> findNavController()
                .navigate(SalesHistoryFragmentDirections
                    .actionSalesHistoryFragmentToSellerProfileFragment(user = user, toReviews = false))}, {
                        findNavController().navigate(SalesHistoryFragmentDirections.actionSalesHistoryFragmentToOrderDetailsFragment(it))
            }, {}).also{salesAdapter = it}
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

    private fun initToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.toolbarLike.setOnClickListener{
            findNavController().navigate(R.id.favoritesFragment)
        }
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

    override fun onItemsLoaded(
        cartItems: List<CartItem>,
        activeOrders: List<Order>,
        activeSales: List<Order>,
        inactiveOrders: List<Order>,
        inactiveSales: List<Order>
    ) {
        /*menuAdapter.changeCartItemsSize(cartItems.size)
        menuAdapter.changeOrdersItemsSize(activeOrders.size)
        menuAdapter.changeSalesItemsSize(activeSales.size)
        menuAdapter.changeOrderHistorySize(inactiveOrders.size)
        menuAdapter.changeSalesHistorySize(inactiveSales.size)*/
    }

    override fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>) {
        binding.toolbar.sliderMenu.adapter = adapter
        binding.toolbar.sliderMenu.scrollToPosition(4)
    }

    override fun navigate(pageId: Int?) {
        val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
        if (pageId != null) {
            findNavController().navigate(pageId, null, navOptions)
        }else findNavController().popBackStack()
    }


}