package com.project.morestore.fragments.orders.active

import android.content.Context
import android.graphics.Bitmap
import com.project.morestore.R
import com.project.morestore.adapters.cart.OrderClickListener
import com.project.morestore.adapters.cart.OrdersAdapter
import com.project.morestore.dialogs.YesNoDialog
import com.project.morestore.models.cart.OrderItem
import com.project.morestore.models.cart.OrderStatus
import moxy.MvpPresenter

class OrdersActivePresenter(val context: Context)
    : MvpPresenter<OrdersActiveView>() {

    private lateinit var adapter: OrdersAdapter

    override fun attachView(view: OrdersActiveView) {
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

            val listener = object : OrderClickListener {
                override fun acceptMeeting(orderItem: OrderItem) {}
                override fun declineMeeting(orderItem: OrderItem) {}

                override fun acceptOrder(orderItem: OrderItem) {
                    val acceptDialog = YesNoDialog(
                            context.getString(R.string.active_order_accept_dialog_title),
                            null,
                            object : YesNoDialog.onClickListener {
                                override fun onYesClick() {
                                }

                                override fun onNoClick() {
                                }

                            })

                    viewState.showAcceptOrderDialog(acceptDialog)
                }

                override fun reportProblem(orderItem: OrderItem) {
                    viewState.navigate(orderItem.id)
                }
            }

            adapter = OrdersAdapter(
                    listOf(
                            OrderItem(
                                    123123121,
                                    bmp,
                                    "Екатерина",
                                    bmp,
                                    "Кеды",
                                    100,
                                    "18 апреля",
                                    "CДЭK",
                                    OrderStatus.RECEIVED,
                                    null,
                                    null
                            ),
                            OrderItem(
                                    123123121,
                                    bmp,
                                    "Екатерина",
                                    bmp,
                                    "Кеды",
                                    100,
                                    "18 апреля",
                                    "CДЭK",
                                    OrderStatus.CHANGE_MEETING,
                                    null,
                                    null
                            ),
                            OrderItem(
                                    123123121,
                                    bmp,
                                    "Екатерина",
                                    bmp,
                                    "Кеды",
                                    100,
                                    "18 апреля",
                                    "CДЭK",
                                    OrderStatus.AT_COURIER,
                                    null,
                                    null
                            )
                    ),
                    listener
            )
        }
        viewState.initActiveOrders(adapter)
    }
}