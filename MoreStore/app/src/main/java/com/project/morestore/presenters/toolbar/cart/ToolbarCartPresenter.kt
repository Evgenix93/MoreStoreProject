package com.project.morestore.presenters.toolbar.cart

import android.content.Context
import com.project.morestore.R
import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.models.Order
import com.project.morestore.models.cart.CartItem
import com.project.morestore.models.slidermenu.OrdersSliderMenu
import com.project.morestore.models.slidermenu.SliderMenu
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class ToolbarCartPresenter(val context: Context, val selectedMenu: OrdersSliderMenu)
    : MvpPresenter<ToolbarCartView>() {

    private lateinit var adapter: SliderMenuAdapter<OrdersSliderMenu>
    private val ordersRepository = OrdersRepository(context)
    private val authRepository = AuthRepository(context)

    override fun attachView(view: ToolbarCartView) {
        super.attachView(view)
        initMenu()
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      public
    ///////////////////////////////////////////////////////////////////////////

    fun onBackClick() {
        viewState.navigate(null)
    }



    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    private fun initMenu() {
        presenterScope.launch {
            //val cartItems = getCartItems()
            //val orderItems = getOrderItems()
            adapter = SliderMenuAdapter(
                listOf(
                        SliderMenu(
                                R.drawable.ic_cart,
                                context.getString(R.string.orders_menu_cart),
                                0u,
                                selectedMenu == OrdersSliderMenu.CART,
                                OrdersSliderMenu.CART
                        ),
                        SliderMenu(
                                R.drawable.ic_package,
                                context.getString(R.string.orders_menu_orders),
                                0u,
                                selectedMenu == OrdersSliderMenu.ORDERS,
                                OrdersSliderMenu.ORDERS
                        ),
                        SliderMenu(
                            R.drawable.ic_coin,
                            context.getString(R.string.sale),
                            0u,
                            selectedMenu == OrdersSliderMenu.SALES,
                            OrdersSliderMenu.SALES
                        ),
                        SliderMenu(
                                R.drawable.ic_reverse_clockwise,
                                context.getString(R.string.orders_menu_purchase_history),
                                0u,
                                selectedMenu == OrdersSliderMenu.ORDERS_HISTORY,
                                OrdersSliderMenu.ORDERS_HISTORY
                        ),
                    SliderMenu(
                        R.drawable.ic_clockwise,
                        context.getString(R.string.orders_menu_sales_history),
                        0u,
                        selectedMenu == OrdersSliderMenu.SALES_HISTORY,
                        OrdersSliderMenu.SALES_HISTORY
                    )
                )
        ) {
            when (it) {
                OrdersSliderMenu.SALES -> {
                  viewState.navigate(R.id.salesActiveFragment)
                }
                OrdersSliderMenu.SALES_HISTORY -> {
                    viewState.navigate(R.id.salesHistoryFragment)
                }
                OrdersSliderMenu.CART -> {
                    viewState.navigate(R.id.ordersCartFragment)
                }
                OrdersSliderMenu.ORDERS_HISTORY -> {
                    viewState.navigate(R.id.ordersHistoryFragment)
                }
                OrdersSliderMenu.ORDERS -> {
                    viewState.navigate(R.id.ordersActiveFragment)
                }
            }
        }

            viewState.initMenuAdapter(adapter)
            val cartItems = getCartItems()
            val orderItems = getOrderItems()
            adapter.changeCartItemsSize(cartItems?.size ?: 0)
            adapter.changeOrdersItemsSize(orderItems?.size ?: 0)
            viewState.initMenuAdapter(adapter)
        }
    }

    private suspend fun getCartItems(): List<CartItem>?{
        val response = ordersRepository.getCartItems(authRepository.getUserId())
        return if(response?.code() == 200 ) response.body() else null

    }

    private suspend fun getOrderItems(): List<Order>?{
        val response = ordersRepository.getAllOrders()
        return if(response?.code() == 200 ) response.body() else null

    }
}