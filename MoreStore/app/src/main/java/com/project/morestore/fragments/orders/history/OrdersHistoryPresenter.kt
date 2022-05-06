package com.project.morestore.fragments.orders.history

import android.content.Context
import android.graphics.Bitmap
import com.project.morestore.adapters.cart.OrdersHistoryAdapter
import com.project.morestore.models.cart.OrderHistoryItem
import com.project.morestore.models.cart.OrderHistoryStatus
import moxy.MvpPresenter

class OrdersHistoryPresenter(context: Context)
    : MvpPresenter<OrdersHistoryView>() {

    private lateinit var adapter: OrdersHistoryAdapter

    override fun attachView(view: OrdersHistoryView) {
        super.attachView(view)

        initContent();
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    private fun initContent() {

        if (!this::adapter.isInitialized) {
            val conf = Bitmap.Config.ARGB_8888 // see other conf types
            val bmp = Bitmap.createBitmap(100, 100, conf)

            adapter = OrdersHistoryAdapter(
                    listOf(
                            OrderHistoryItem(
                                    "123123121",
                                    bmp,
                                    "Екатерина",
                                    bmp,
                                    "Кеды",
                                    3000,
                                    "19 февраля",
                                    "Lorem ipsum",
                                    OrderHistoryStatus.COMPLETED
                            ),
                            OrderHistoryItem(
                                    "123123121",
                                    bmp,
                                    "Екатерина",
                                    bmp,
                                    "Кеды",
                                    3000,
                                    "19 февраля",
                                    "Lorem ipsum",
                                    OrderHistoryStatus.COMPLETED
                            ),
                            OrderHistoryItem(
                                    "123123121",
                                    bmp,
                                    "Екатерина",
                                    bmp,
                                    "Кеды",
                                    3000,
                                    "19 февраля",
                                    "Lorem ipsum",
                                    OrderHistoryStatus.COMPLETED
                            )
                    )
            ) {

            }
        }
        viewState.initOrdersHistory(adapter)
    }
}