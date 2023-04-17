package com.project.morestore.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.presentation.adapters.SalesAdapter
import com.project.morestore.presentation.adapters.SliderMenuAdapter
import com.project.morestore.databinding.FragmentOrdersBinding
import com.project.morestore.data.models.*
import com.project.morestore.data.models.cart.CartItem
import com.project.morestore.data.models.slidermenu.OrdersSliderMenu
import com.project.morestore.data.models.slidermenu.SliderMenu
import com.project.morestore.domain.presenters.SalesPresenter
import com.project.morestore.domain.presenters.toolbar.cart.ToolbarCartPresenter
import com.project.morestore.presentation.fragments.orders.active.OrdersActiveFragmentDirections
import com.project.morestore.presentation.mvpviews.SalesMvpView
import com.project.morestore.presentation.mvpviews.ToolbarCartView
import com.project.morestore.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class SalesActiveFragment: MvpAppCompatFragment(R.layout.fragment_orders), SalesMvpView, ToolbarCartView {
    private val binding: FragmentOrdersBinding by viewBinding()
    private var salesAdapter: SalesAdapter by autoCleared()
    private var menuAdapter: SliderMenuAdapter<OrdersSliderMenu> by autoCleared()
    @Inject
    lateinit var salesPresenter: SalesPresenter
    @Inject
    lateinit var toolBarPresenterRef: ToolbarCartPresenter
    private val presenter by moxyPresenter { salesPresenter }
    private val toolBarPresenter by moxyPresenter { toolBarPresenterRef }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBarPresenter.initMenu(OrdersSliderMenu.SALES)
        initToolbar()
        //initMenuList()
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
                       ChatFragment.FROM_ORDERS to true,
                       Chat::class.java.simpleName to Chat.Deal::class.java.simpleName
                   )
               )
           }, acceptDeal = {
                binding.loader.isVisible = true
                presenter.submitBuy(it)
           }, cancelDeal = {
               presenter.cancelBuyRequest(it)
           },
           onProfileClick = {user -> findNavController()
               .navigate(SalesActiveFragmentDirections.actionSalesActiveFragmentToSellerProfileFragment(user = user, toReviews = false))},
               onClick = {
                   findNavController().navigate(SalesActiveFragmentDirections.actionSalesActiveFragmentToOrderDetailsFragment(it))
               },
           onDeliveryCreateClick = {order -> findNavController()
               .navigate(SalesActiveFragmentDirections.actionSalesActiveFragmentToCreateDeliveryFragment(order))}).also{salesAdapter = it}
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
            val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
            when (it) {
                OrdersSliderMenu.SALES_HISTORY -> {
                    findNavController().navigate(R.id.salesHistoryFragment, null, navOptions)
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
                else -> {}
            }
        }.also{menuAdapter = it}
        binding.toolbar.sliderMenu.scrollToPosition(2)
    }

    private fun getSales(){
      binding.loader.isVisible = true
      presenter.getSales(false)
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

    private fun updateSalesList(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>, dialogs: List<DialogWrapper>){
        salesAdapter.updateList(sales, addresses, users, dialogs)
    }

    override fun onSalesLoaded(sales: List<Order>, addresses: List<OfferedOrderPlace>, users: List<User?>, dialogs: List<DialogWrapper>) {
        binding.loader.isVisible = false
        updateSalesList(sales, addresses, users, dialogs)
    }


    override fun onError(message: String) {
        binding.loader.isVisible = false
        showToast(message)
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
        /*menuAdapter.changeCartItemsSize(cartItems.size)
        menuAdapter.changeOrdersItemsSize(activeOrders.size)
        menuAdapter.changeSalesItemsSize(activeSales.size)
        menuAdapter.changeOrderHistorySize(inactiveOrders.size)
        menuAdapter.changeSalesHistorySize(inactiveSales.size)*/
    }

    override fun onDealStatusChanged() {
        getSales()
    }

    override fun initMenuAdapter(adapter: SliderMenuAdapter<OrdersSliderMenu>) {
        binding.toolbar.sliderMenu.adapter = adapter
        binding.toolbar.sliderMenu.scrollToPosition(2)
    }

    override fun navigate(pageId: Int?) {
        val navOptions =  NavOptions.Builder().setPopUpTo(findNavController().previousBackStackEntry!!.destination.id, false).build()
        if (pageId != null) {
            findNavController().navigate(pageId, null, navOptions)
        }else findNavController().popBackStack()
    }
}