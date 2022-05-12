package com.project.morestore.presenters.toolbar.cart

import android.content.Context
import com.project.morestore.R
import com.project.morestore.adapters.SliderMenuAdapter
import com.project.morestore.models.slidermenu.OrdersSliderMenu
import com.project.morestore.models.slidermenu.SliderMenu
import moxy.MvpPresenter

class ToolbarCartPresenter(val context: Context, val selectedMenu: OrdersSliderMenu)
    : MvpPresenter<ToolbarCartView>() {

    private lateinit var adapter: SliderMenuAdapter<OrdersSliderMenu>

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
        adapter = SliderMenuAdapter(
                listOf(
                        SliderMenu(
                                R.drawable.ic_cart,
                                context.getString(R.string.orders_menu_cart),
                                2u,
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
                                4u,
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
    }
}