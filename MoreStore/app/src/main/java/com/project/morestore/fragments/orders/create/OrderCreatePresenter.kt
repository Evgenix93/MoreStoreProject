package com.project.morestore.fragments.orders.create

import android.content.Context
import com.project.morestore.models.Product
import moxy.MvpPresenter

class OrderCreatePresenter(context: Context)
    : MvpPresenter<OrderCreateView>() {

    ///////////////////////////////////////////////////////////////////////////
    //                      public
    ///////////////////////////////////////////////////////////////////////////

    fun onBackClick() {
        viewState.navigate(null)
    }

    fun setProduct(product: Product) {
        viewState.setProductInfo(product)
    }

    fun onCancelOrderCreateClick() {
        viewState.navigate(null)
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

}